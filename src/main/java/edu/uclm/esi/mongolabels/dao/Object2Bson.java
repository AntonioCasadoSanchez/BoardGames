package edu.uclm.esi.mongolabels.dao;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

import edu.uclm.esi.mongolabels.labels.Bsonable;

public class Object2Bson {
	
	public static BsonDocument getBsonDocument(Object object) throws Exception {
		if (!Common.isBsonable(object))
			throw new Exception("The class " + object.getClass().getName() + " has no @Bsonable fields");
		BsonDocument bso=new BsonDocument();
		bso.put("className", new BsonString(object.getClass().getName()));
		Class<?> clazz=object.getClass();
		Enumeration<Field> fields=Common.getFields(clazz).elements();
		while (fields.hasMoreElements()) {
			Field field=fields.nextElement();
			field.setAccessible(true);
			if (!field.isAnnotationPresent(Bsonable.class) || field.get(object)==null)
				continue;
			try {
				put(bso, object, field);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bso;
	}
	
	@SuppressWarnings("unchecked")
	private static void put(BsonDocument bso, Object object, Field field) throws Exception {
		String fieldName=field.getName();
		Object value=field.get(object);
		Bsonable annotation = field.getAnnotation(Bsonable.class);
		BsonValue bsonValue=null;
		if (annotation!=null && annotation.type().equals(Bsonable.StatePattern)) {
			bsonValue = new BsonString(value.getClass().getName());
		} else if (annotation!=null && annotation.type().equals(Bsonable.FK)) {
			if (Collection.class.isAssignableFrom(value.getClass())) {
				Collection<Object> collection=(Collection<Object>) value;
				BsonArray bsa=new BsonArray();
				Iterator<Object> iCollection = collection.iterator();
				Object contained;
				while (iCollection.hasNext()) {
					contained=iCollection.next();
					Object idValue = findId(contained);
					BsonDocument externalObject=new BsonDocument();
					externalObject.append("className", new BsonString(contained.getClass().getName()));
					externalObject.append("_id", getBsonValue(idValue));
					externalObject.append("isFK", new BsonBoolean(true));
					bsa.add(externalObject);
				}
				bsonValue=bsa;
			} else
				bsonValue = getBsonValue(findId(value));
		} else {
			bsonValue = getBsonValue(value);
		}
		if (bsonValue==null)
			throw new Exception("The type " + value.getClass().getName() + " is not supported yet");
		bso.put(fieldName, bsonValue);
	}
	
	private static Object findId(Object object) throws Exception {
		Class<?> clazz=object.getClass();
		Hashtable<String, Field> fields = Common.getFields(clazz);
		Field idField=fields.get("_id");
		if (idField==null)
			throw new Exception("The _id field has not been found in " + clazz.getName());			
		if (idField.getType()!=ObjectId.class)
			throw new Exception("The _id field has been found in " + clazz.getName() + ", but it is not of ObjectId type");
		idField.setAccessible(true);
		return idField.get(object);
	}

	private static BsonValue getBsonValue(Object value) throws Exception {
		Class<?> type = value.getClass();
		if (type==ObjectId.class)
			return new BsonObjectId((ObjectId) value);
		if (type==char.class || type==Character.class) 
			return new BsonString((String) ("" + value));
		if (type==int.class || type==Integer.class)
			return new BsonInt32((int) value);
		if (type==long.class || type==Long.class) 
			return new BsonInt64((long) value);
		if (type==float.class || type==Float.class) 
			return new BsonDouble((double) value);
		if (type==double.class || type==Double.class) 
			return new BsonDouble((double) value);
		if (type==byte.class || type==Byte.class) 
			return new BsonInt32((byte) value);
		if (type==boolean.class || type==Boolean.class) 
			return new BsonBoolean((boolean) value);
		if (type==String.class) 
			return new BsonString(value.toString());
		if (type.isArray()) {
			Object[] values=(Object[]) value;
			BsonArray bsa=new BsonArray();
			for (int i=0; i<values.length; i++) {
				BsonValue bsonValue=getBsonValue(values[i]);
				bsa.add(bsonValue);
			}
			return bsa;
		}
		if (Collection.class.isAssignableFrom(type)) {
			BsonArray bsa=new BsonArray();
			Collection<?> values=(Collection<?>) value;
			Iterator<?> iValues = values.iterator();
			while (iValues.hasNext()) {
				Object iValue = iValues.next();
				BsonValue bsonValue=getBsonValue(iValue);
				bsa.add(bsonValue);
			}
			return bsa;
		}
		if (Common.isBsonable(value))
			return getBsonDocument(value);
		return null;
	}
}
