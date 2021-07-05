package com.s8.blocks.joos.fields.objects;

import java.io.IOException;
import java.lang.reflect.Field;

import com.s8.blocks.joos.JOOS_Lexicon;
import com.s8.blocks.joos.JOOS_Type;
import com.s8.blocks.joos.composing.ComposingScope;
import com.s8.blocks.joos.composing.JOOS_ComposingException;
import com.s8.blocks.joos.fields.FieldHandler;
import com.s8.blocks.joos.parsing.JOOS_ParsingException;
import com.s8.blocks.joos.parsing.ObjectScope;
import com.s8.blocks.joos.parsing.ParsingScope;
import com.s8.blocks.joos.parsing.ParsingScope.OnParsedObject;
import com.s8.blocks.joos.types.JOOS_CompilingException;
import com.s8.blocks.joos.types.TypeHandler;

public class ObjectFieldHandler extends FieldHandler {

	/**
	 * 
	 */
	public Class<?> fieldType;

	public ObjectFieldHandler(String name, Field field) {
		super(name, field);
		this.fieldType = field.getType();
	}

	public void set(Object object, Object child) throws IllegalArgumentException, IllegalAccessException {
		field.set(object, child);
	}

	@Override
	public Class<?> getSubType() {
		return fieldType;
	}



	public Object get(Object object) throws IllegalArgumentException, IllegalAccessException {
		return field.get(object);
	}

	@Override
	public void subDiscover(JOOS_Lexicon context) throws JOOS_CompilingException {
		if(fieldType.getAnnotation(JOOS_Type.class)!=null) {
			context.discover(fieldType);	
		}
	}

	@Override
	public boolean compose(Object object, ComposingScope scope) throws JOOS_ComposingException, IOException {

		Object value = null;
		try {
			value = field.get(object);
		} 
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new JOOS_ComposingException(e.getMessage());
		}

		if(value!=null) {
			// declare type
			scope.newItem();
			scope.append(name);
			scope.append(':');

			// declare type
			TypeHandler typeHandler = scope.getTypeHandler(value);
			typeHandler.compose(value, scope);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public ParsingScope openScope(Object object) {
		return new ObjectScope(new OnParsedObject() {
			@Override
			public void set(Object value) throws JOOS_ParsingException {
				try {
					ObjectFieldHandler.this.set(object, value);
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					throw new JOOS_ParsingException("Failed to set object due to "+e.getMessage());
				}
			}	
		});
	}
}
