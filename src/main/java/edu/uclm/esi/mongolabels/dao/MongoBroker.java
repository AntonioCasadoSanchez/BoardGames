package edu.uclm.esi.mongolabels.dao;

import java.util.List;

import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoBroker {
	private String serverUri;
	
	private MongoClient client;
	private MongoDatabase db;
	private String user="tysweb";
	private String pwd="tysweb20182019";
	
	private MongoBroker() {
		this.serverUri="mongodb://" + user + ":" + pwd + "@ds159782.mlab.com:59782/juegos";
		MongoClientURI clientUri=new MongoClientURI(this.serverUri);
		this.client=new MongoClient(clientUri);
		String dbName=serverUri.substring(serverUri.lastIndexOf("/")+1);
		this.db=this.client.getDatabase(dbName);
	}
	
	public static String encrypt(String text) {
		return text;
		/*MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			byte[] hash = digest.digest(text.getBytes());
			return new String(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;*/
	}

	private static class MongoBrokerHolder {
		static MongoBroker singleton = new MongoBroker();
	}

	public static MongoBroker get() {
		return MongoBrokerHolder.singleton;
	}

	public BsonObjectId insert(Object object) throws Exception {
		BsonDocument bso=Object2Bson.getBsonDocument(object);
		Class<?> collectionClass=getCollectionClass(object.getClass());
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionClass.getSimpleName(), BsonDocument.class);
		collection.insertOne(bso);
		return bso.getObjectId("_id");
	}
	
	public BsonObjectId insertBson(Class<?> clazz, BsonDocument bso) throws Exception {
		Class<?> collectionClass=getCollectionClass(clazz);
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionClass.getSimpleName(), BsonDocument.class);
		collection.insertOne(bso);
		return bso.getObjectId("_id");
	}
	
	public BsonObjectId insertBson(String collectionName, BsonDocument bso) throws Exception {
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionName, BsonDocument.class);
		collection.insertOne(bso);
		return bso.getObjectId("_id");
	}
	
	public Object load(String className, ObjectId _id) throws Exception {
		return load(Class.forName(className), _id);
	}

	public Object load(Class<?> clazz, ObjectId _id) throws Exception {
		Class<?> collectionClass=getCollectionClass(clazz);
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionClass.getSimpleName(), BsonDocument.class);
		BsonDocument criterion=new BsonDocument();
		criterion.append("_id", new BsonObjectId(_id));
		FindIterable<BsonDocument> iterator = collection.find(criterion);
		if (iterator==null)
			return null;
		BsonDocument bso=iterator.first();
		return Bson2Object.getObject(bso);
	}
	
	public BsonDocument loadBinary(String collectionName, BsonDocument criterion) throws Exception {
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionName, BsonDocument.class);
		BsonDocument bsoBytes=new BsonDocument();
		bsoBytes.append("fileName", new BsonInt32(1));
		bsoBytes.append("bytes", new BsonInt32(1));
		bsoBytes.append("_id", new BsonInt32(0));
		FindIterable<BsonDocument> iterator = collection.find(criterion).projection(bsoBytes);
		if (iterator==null)
			return null;
		BsonDocument bso=iterator.first();
		return bso;
	}
	
	public BsonDocument loadOne(String collectionName, BsonDocument criterion) throws Exception {
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionName, BsonDocument.class);
		FindIterable<BsonDocument> iterator = collection.find(criterion);
		if (iterator==null)
			return null;
		BsonDocument bso=iterator.first();
		return bso;
	}
	
	public Object loadOne(Class<?> clazz, BsonDocument criterion) throws Exception {
		Class<?> collectionClass=getCollectionClass(clazz);
		String collectionClassName=collectionClass.getSimpleName();
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionClassName, BsonDocument.class);
		FindIterable<BsonDocument> iterator = collection.find(criterion);
		if (iterator==null)
			return null;
		BsonDocument bso=iterator.first();
		bso.append("className", new BsonString(clazz.getName()));
		return Bson2Object.getObject(bso);
	}
	
	private Class<?> getCollectionClass(Class<?> clazz) {
		Class<?> collectionClass=clazz;
		while (collectionClass.getSuperclass()!=java.lang.Object.class)
			collectionClass=collectionClass.getSuperclass();
		return collectionClass;
	}

	public void drop(String collectionName) {
		this.db.getCollection(collectionName).drop();
	}
	
	public JSONArray loadAll(Object mainObject, String relatedObjectsClass, String fkField, String... fieldsToRead) throws Exception {
		BsonDocument bsonMainObject=Object2Bson.getBsonDocument(mainObject);
		BsonObjectId _id = bsonMainObject.getObjectId("_id");
		BsonDocument criterio=new BsonDocument();
		criterio.append(fkField, _id);
		
		MongoCollection<BsonDocument> collection=this.db.getCollection(relatedObjectsClass, BsonDocument.class);
		MongoCursor<BsonDocument> elements;
		if (fieldsToRead!=null && fieldsToRead.length>0) {
			BsonDocument bsoFieldsToRead=new BsonDocument();
			boolean _idRequested=false;
			for (int i=0; i<fieldsToRead.length; i++) {
				bsoFieldsToRead.append(fieldsToRead[i], new BsonInt32(1));
				if (fieldsToRead[i].equals("_id"))
					_idRequested=true;
			}
			if (!_idRequested)
				bsoFieldsToRead.append("_id", new BsonInt32(0));
			elements = collection.find(criterio).projection(bsoFieldsToRead).iterator();
		} else 
			elements = collection.find(criterio).iterator();
		JSONArray result=new JSONArray();
		JSONObject object;
		while (elements.hasNext()) {
			object=new JSONObject(elements.next().toJson());
			result.put(object);
		}
		return result;		
	}

	public JSONArray loadAll(Object mainObject, Class<?> relatedObjectsClass, String fkField) throws Exception {
		String relatedObjectsClassName=relatedObjectsClass.getSimpleName();
		return loadAll(mainObject, relatedObjectsClassName, fkField);
	}
	
	public void insertBinary(ObjectId idOwner, String collectionName, String fileName, byte[] bytes, BsonValue... values) throws Exception {
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionName, BsonDocument.class);
		BsonDocument bso=new BsonDocument();
		bso.append("bytes", new BsonBinary(bytes));
		bso.append("owner", new BsonObjectId(idOwner));
		bso.append("fileName", new BsonString(fileName));
		if (values.length%2!=0)
			throw new Exception("Error in insertBinary: bad number of parameters");
		for (int i=0; i<values.length; i++) {
			bso.append(values[i].asString().getValue(), values[i+1]);
			i++;
		}
		collection.insertOne(bso);
	}

	public void delete(String collectionName, BsonDocument criterion) {
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionName, BsonDocument.class);
		collection.deleteMany(criterion);
	}

	public JSONArray loadAll(String collectionName, ObjectId owner, String... fieldsToRead) throws JSONException {
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionName, BsonDocument.class);
		BsonDocument criterion=new BsonDocument();
		criterion.put("owner", new BsonObjectId(owner));
		BsonDocument bsoFieldsToRead=new BsonDocument();
		BsonDocument sortCriterion=new BsonDocument();
		for (int i=0; i<fieldsToRead.length; i++) {
			bsoFieldsToRead.append(fieldsToRead[i], new BsonInt32(1));
			sortCriterion.append(fieldsToRead[i], new BsonInt32(1));
		}
		MongoCursor<BsonDocument> elements=collection.find(criterion).projection(bsoFieldsToRead).iterator();
		JSONObject object;
		JSONArray result=new JSONArray();
		while (elements.hasNext()) {
			object=new JSONObject(elements.next().toJson());
			result.put(object);
		}
		return result;
	}

	public MongoCursor<BsonDocument> mapReduce(String collectionName, ObjectId objectId, int numberOfMapFields, String... fields) {
		this.drop("prueba");
		String keyFields = "{\n";
		for (int i=0; i<numberOfMapFields; i++)
			keyFields=keyFields + "\t\t" + fields[i] + " : this." + fields[i] + ",\n";
		keyFields=keyFields.substring(0, keyFields.length()-2);
		keyFields=keyFields + "\n\t}";
		
		String fieldsToEmit = "{\n";
		for (int i=numberOfMapFields; i<fields.length; i++)
			fieldsToEmit=fieldsToEmit + "\t\t" + fields[i] + " : this." + fields[i] + ",\n";
		fieldsToEmit=fieldsToEmit.substring(0, fieldsToEmit.length()-2);
		fieldsToEmit+="\n\t}";
		
		String map="function() {\n" +
			"\tvar clave=" + keyFields + ";\n" +
			"\tvar objeto=" + fieldsToEmit + ";\n" +
			"\temit(clave, objeto);\n" +
			"\temit(clave, objeto);\n" +
			"}\n";
		String reduce="function(clave, objetos) {\n" +
			"\tvar resultado = {\n" +
			"\t\tn : objetos.length/2\n" +
			"\t};\n" +
			"\treturn resultado;\n" +
			"}";
		MongoCollection<BsonDocument> collection=this.db.getCollection(collectionName, BsonDocument.class);
		BsonDocument criterion=new BsonDocument();
		criterion.put("owner", new BsonObjectId(objectId));
		MapReduceIterable<BsonDocument> mrResult = collection.mapReduce(map, reduce).filter(criterion).collectionName("prueba");
		return mrResult.iterator();
	}

	public MongoCursor<BsonDocument> aggregate(String collectionName, List<BsonDocument> criterion) {
		return this.db.getCollection(collectionName).aggregate(criterion, BsonDocument.class).iterator();
	}
}
