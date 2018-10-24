package edu.uclm.esi.mongolabels.dao;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.uclm.esi.mongolabels.labels.Bsonable;

public class Common {
	public static boolean isBsonable(Object object) {
		Class<?> clazz=object.getClass();
		Enumeration<Field> eFields = getFields(clazz).elements();
		Field field;
		while (eFields.hasMoreElements()) {
			field=eFields.nextElement();
			if (field.isAnnotationPresent(Bsonable.class))
				return true;
		}
		return false;
	}
	
	public static Hashtable<String, Field> getFields(Class<?> clazz) {
		Hashtable<String, Field> fields=new Hashtable<>();
		loadFields(fields, clazz);
		return fields;
	}

	private static void loadFields(Hashtable<String, Field> fields, Class<?> clazz) {
		Field[] thisFields=clazz.getDeclaredFields();
		for (int i=0; i<thisFields.length; i++)
			fields.put(thisFields[i].getName(), thisFields[i]);
		Class<?> superclazz=clazz.getSuperclass();
		if (superclazz!=Object.class) 
			loadFields(fields, superclazz);
	}
}
