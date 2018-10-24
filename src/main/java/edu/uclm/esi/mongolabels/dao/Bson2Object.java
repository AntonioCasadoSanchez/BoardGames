package edu.uclm.esi.mongolabels.dao;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

import edu.uclm.esi.mongolabels.labels.Bsonable;

public class Bson2Object {
	
	public static Object getObject(BsonDocument bso) throws Exception {
		String className=bso.getString("className").getValue();
		Class<?> clazz=Class.forName(className);
		if (Modifier.isAbstract(clazz.getModifiers()))
			throw new Exception("The class " + clazz.getName() + " is abstract and cannot be instantiated");
		
		Object object;
		try {
			object=clazz.newInstance();
		}
		catch (InstantiationException e) {
			throw new Exception("The class " + className + " requires a public contructor with no paremeters");
		}
		if (!Common.isBsonable(object))
			throw new Exception("The class " + object.getClass().getName() + " has no @Bsonable fields");
		
		if (bso.containsKey("isFK")) {
			ObjectId _id=bso.getObjectId("_id").getValue();
			Object containedObject=MongoBroker.get().load(className, _id);
			object=containedObject;
		} else {
			Hashtable<String, Field> classFields = Common.getFields(clazz);
			Iterator<String> bsonFieldNames = bso.keySet().iterator();
			String bsonFieldName;
			Field field;
			while (bsonFieldNames.hasNext()) {
				bsonFieldName=bsonFieldNames.next();
				field=classFields.get(bsonFieldName);
				if (field==null)
					continue;
				Bsonable annotation = field.getAnnotation(Bsonable.class);
				field.setAccessible(true);
				BsonValue bsonValue=bso.get(bsonFieldName);
				if (annotation!=null && annotation.type().equals(Bsonable.StatePattern)) {
					Class<?> stateClass=Class.forName(bsonValue.asString().getValue());
					Object state=stateClass.newInstance();
					field.set(object, state);
				} else {
					set(object, field, bsonValue, bsonFieldName);
				}
			}
		}
		return object;
	}

	private static void set(Object object, Field field, BsonValue bsonValue, String bsonFieldName) throws Exception {
		Class<?> fieldType=field.getType();
		Object value=null;
		if (fieldType==BsonObjectId.class)
			value=bsonValue.asObjectId().getValue();
		else if (fieldType==ObjectId.class)
			value=bsonValue.asObjectId().getValue();
		else if (fieldType.isPrimitive() || Number.class.isAssignableFrom(fieldType))
			value=getPrimitiveValue(bsonValue);
		else if (fieldType==char.class || fieldType==Character.class)
			value=getCharValue(bsonValue);
		else if (fieldType==String.class)
			value=getStringValue(bsonValue);
		else if (Collection.class.isAssignableFrom(fieldType)) {
			Vector<Object> vector=getCollectionValue(bsonValue);
			if (vector==null)
				return;
			Collection<Object> collection=(Collection<Object>) field.getType().newInstance();
			for (int i=0; i<vector.size(); i++)
				collection.add(vector.get(i));
			field.set(object, collection);
			return;
		} else if (fieldType.isArray()) {
			Vector<Object> vector=getCollectionValue(bsonValue);
			if (vector==null)
				return;
			Object collection = Array.newInstance(field.getType().getComponentType(), vector.size());
			for (int i=0; i<vector.size(); i++) {
				Object x=vector.get(i);
				Array.set(collection, i, x);
			}
			field.set(object, collection);
			return;
		}
		if (value==null)
			return;
		field.set(object, value);
	}
	
	private static Vector<Object> getCollectionValue(BsonValue bsonValue) throws Exception {
		Vector<Object> collection=new Vector<>();
		BsonArray bsa=bsonValue.asArray();
		BsonValue bso;
		Object object=null;
		for (int i=0; i<bsa.size(); i++) {
			bso= bsa.get(i);
			if (bso.isDocument() && bso.asDocument().containsKey("className"))
				object=getObject(bso.asDocument());
			else if (bso.isBoolean())
				object=bso.asBoolean().getValue();
			else if (bso.isInt32())
				object=bso.asInt32().getValue();
			else if (bso.isInt64())
				object=bso.asInt64().getValue();
			else if (bso.isDouble())
				object=bso.asDouble().getValue();
			else if (bso.isString())
				object=bso.asString().getValue();
			if (object!=null)
				collection.add(object);
		}
		return collection;
	}

	private static Character getCharValue(BsonValue bsonValue) {
		return bsonValue.asString().getValue().charAt(0);
	}
	
	private static String getStringValue(BsonValue bsonValue) {
		return bsonValue.asString().getValue();
	}

	private static Object getPrimitiveValue(BsonValue bsonValue) throws Exception {
		if (bsonValue.isBoolean()) 
			return bsonValue.asBoolean().getValue();
		if (bsonValue.isInt32())
			return bsonValue.asInt32().getValue();
		else if (bsonValue.isInt64())
			return bsonValue.asInt64().getValue();
		else if (bsonValue.isDouble())
			return bsonValue.asDouble().getValue();
		return null;
	}
}
