package com.jadaptive.app.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.db.SearchField.Type;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.repository.UUIDDocument;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;

@Repository
public class DocumentDatabaseImpl implements DocumentDatabase {

	@Autowired
	protected MongoDatabaseService mongo;
	
	private MongoCollection<Document> getCollection(String table, String database) {
		MongoDatabase db = mongo.getClient().getDatabase(database);
		return db.getCollection(table);
	}

	@Override
	public void createTextIndex(String fieldName, String table, String database) {
		String indexName = "text_" + fieldName;
		MongoCollection<Document> collection = getCollection(table, database);
		collection.createIndex(Indexes.text(fieldName), new IndexOptions().name(indexName));
	}
	
	@Override
	public void createIndex(String table, String database, String... fieldNames) {
		String indexName = "index_" + StringUtils.join(fieldNames, "_");
		MongoCollection<Document> collection = getCollection(table, database);
		collection.createIndex(Indexes.ascending(fieldNames), new IndexOptions().name(indexName));
	}
	
	@Override
	public void createUniqueIndex(String table, String database, String...fieldNames) {
		String indexName = "unique_" + StringUtils.join(fieldNames, "_");
		MongoCollection<Document> collection = getCollection(table, database);
		IndexOptions indexOptions = new IndexOptions().unique(true).name(indexName);
		collection.createIndex(Indexes.ascending(fieldNames), indexOptions);
	}
	
	@Override
	public <E extends UUIDDocument> void insertOrUpdate(E obj, Document document, String table, String database) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		
		
		Date now = new Date();
		if(obj instanceof AbstractUUIDEntity) {
			document.put("lastModified", now);
			if(!document.containsValue("created")) {
				document.put("created", now);
			}
		}
		
		if(StringUtils.isBlank(obj.getUuid())) {
			
			assertUniqueConstraints(collection, document);
			
			
			obj.setUuid(UUID.randomUUID().toString());
			document.put("_id", obj.getUuid());
			collection.insertOne(document);			
		} else {
			collection.replaceOne(Filters.eq("_id", obj.getUuid()), 
					document, new ReplaceOptions().upsert(true));
		}
	}

	private void assertUniqueConstraints(MongoCollection<Document> collection, Document document) {
		
		for(Document index : collection.listIndexes()) {
			if(index.getBoolean("unique", false)) {
				Document key = (Document) index.get("key");
				if(key.size()==1) {
					String field = key.keySet().iterator().next();
					if(collection.countDocuments(Filters.eq(field, document.get(field))) > 0L) {
						throw new RepositoryException(String.format("An object already exists with the same %s value!", org.apache.commons.text.WordUtils.capitalize(field)));
					}
				}

			}
		}
	}

	@Override
	public Document getByUUID(String uuid, String table, String database) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		FindIterable<Document> result = collection.find(Filters.eq("_id", uuid));
		if(!result.cursor().hasNext()) {
			throw new ObjectNotFoundException(String.format("Id %s entity %s was not found", uuid, table));
		}
		return result.first();
	}

	@Override
	public Document get(String table, String database, SearchField... fields) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		FindIterable<Document> result = collection.find(buildFilter(fields));
		if(!result.cursor().hasNext()) {
			throw new ObjectNotFoundException(String.format("No entity %s was not found for search", table));
		}
		return result.first();
	}
	
	@Override
	public Document max(String table, String database, String field) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		FindIterable<Document> result = collection.find().sort(new BasicDBObject("field", -1));
		if(!result.cursor().hasNext()) {
			throw new ObjectNotFoundException(String.format("No entity %s was not found", table));
		}
		return result.first();
	}
	
	@Override
	public Document min(String table, String database, String field) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		FindIterable<Document> result = collection.find().sort(new BasicDBObject("field", 1));
		if(!result.cursor().hasNext()) {
			throw new ObjectNotFoundException(String.format("No entity %s was not found", table));
		}
		return result.first();
	}
	
	@Override
	public Document find(String field, String value, String table, String database) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		FindIterable<Document> result = collection.find(Filters.eq(field, value));
		if(!result.cursor().hasNext()) {
			throw new ObjectNotFoundException(String.format("%s %s for entity %s was not found", field, value, table));
		}
		return result.first();
	}

	@Override
	public void deleteByUUID(String uuid, String table, String database) {
		
		getByUUID(uuid, table, database);
		MongoCollection<Document> collection = getCollection(table, database);
		collection.deleteOne(Filters.eq("_id", uuid));
	}
	
	@Override
	public void delete(String table, String database, SearchField... fields) {

		MongoCollection<Document> collection = getCollection(table, database);
		DeleteResult result = collection.deleteMany(buildFilter(fields));
		if(result.getDeletedCount() == 0) {
			throw new ObjectException("Object not deleted!");
		}
	}
	
	@Override
	public Iterable<Document> list(String table, String database, SearchField... fields) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		if(fields.length == 0) {
			return collection.find();
		} else {
			return collection.find(buildFilter(fields));
		}
	}
	
	@Override
	public Iterable<Document> search(String table, String database, SearchField...fields) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		if(fields.length == 0) {
			return collection.find();
		} else {
			return collection.find(buildFilter(fields));
		}
	}
	
	@Override
	public Iterable<Document> searchTable(String table, String database, int start, int length, SearchField...fields) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		if(fields.length == 0) {
			return collection.find().skip(start).limit(length);
		} else {
			return collection.find(buildFilter(fields)).skip(start).limit(length);
		}
	}
		
	@Override
	public Long searchCount(String table, String database, SearchField... fields) {
		MongoCollection<Document> collection = getCollection(table, database);
		if(fields.length == 0) {
			return collection.countDocuments();
		} else {
			return collection.countDocuments(buildFilter(fields));
		}
	}
	
	@Override
	public Iterable<Document> table(String table, String searchField, String searchValue, String database, int start, int length) {
		
		MongoCollection<Document> collection = getCollection(table, database);
		searchField = configureSearch(searchField);
		if(StringUtils.isBlank(searchValue)) {
			return collection.find().skip(start).limit(length);
		} else {
			return collection.find(Filters.regex(searchField, searchValue)).skip(start).limit(length);
		}
	}

	@Override
	public Long count(String table, String database, SearchField... fields) {
		MongoCollection<Document> collection = getCollection(table, database);
		if(fields.length > 0) {
			return collection.countDocuments(buildFilter(fields));
		} else {
			return collection.countDocuments();
		}
		
	}
	
	@Override
	public Long count(String table, String searchField, String searchValue, String database) {
		MongoCollection<Document> collection = getCollection(table, database);
		searchField = configureSearch(searchField);
		if(StringUtils.isBlank(searchValue)) {
			return collection.countDocuments();
		} else {
			return collection.countDocuments(Filters.regex(searchField, searchValue));
		}
		
	}
	
	protected String configureSearch(String searchField) {
		if(StringUtils.isBlank(searchField) || searchField.equalsIgnoreCase("UUID")) {
			searchField = "_id";
		}
		return searchField;
	}

	@Override
	public void dropCollection(String table, String database) {
		getCollection(table, database).drop();
	}

	@Override
	public Document getFirst(String uuid, String table, String database) {
		MongoCollection<Document> collection = getCollection(table, database);
		FindIterable<Document> result = collection.find(Filters.eq("_id", uuid));
		if(!result.cursor().hasNext()) {
			throw new ObjectNotFoundException(String.format("id %s for entity %s was not found", uuid, table));
		}
		return result.first();
	}

	@Override
	public void dropDatabase(String database) {
		mongo.getClient().getDatabase(database).drop();
	}
	
	public Bson buildFilter(SearchField...fields) {
		return buildFilter(Type.AND, fields);
	}
	
	public Bson buildFilter(SearchField.Type queryType, SearchField...fields) {
		
		List<Bson> tmp = new ArrayList<>();
		for(SearchField field : fields) {
			switch(field.getSearchType()) {
			case EQUALS:
				tmp.add(Filters.eq(field.getColumn(), field.getValue()[0]));
				break;
			case IN:
				tmp.add(Filters.in(field.getColumn(), field.getValue()));
				break;
			case NOT:
				tmp.add(Filters.ne(field.getColumn(), field.getValue()));
				break;
			case LIKE:
				tmp.add(Filters.regex(field.getColumn(), field.getValue()[0].toString()));
				break;
			case OR:
				tmp.add(buildFilter(SearchField.Type.OR, field.getFields()));
				break;
			case AND:
				tmp.add(buildFilter(SearchField.Type.AND, field.getFields()));
				break;
			}
		}

		if(tmp.size() > 1) {
			switch(queryType) {
			case AND:
				return Filters.and(tmp);
			case OR:
				return Filters.or(tmp);
			default:
				throw new IllegalArgumentException("Invalid use of SearchField.Type");
			}
		}
		
		return tmp.get(0);
	}


}
