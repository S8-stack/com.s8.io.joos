package com.s8.core.io.joos.fields.arrays;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.s8.core.io.joos.ParsingException;
import com.s8.core.io.joos.composing.ComposingScope;
import com.s8.core.io.joos.fields.PrimitivesArrayFieldHandler;
import com.s8.core.io.joos.parsing.StringScope;
import com.s8.core.io.joos.parsing.ArrayScope;
import com.s8.core.io.joos.parsing.JOOS_ParsingException;
import com.s8.core.io.joos.parsing.ParsingScope;


/**
 * 
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public class ShortArrayFieldHandler extends PrimitivesArrayFieldHandler {

	public static class Builder extends PrimitivesArrayFieldHandler.Builder {

		public Builder(String name, Field field) {
			super(field);
			handler = new ShortArrayFieldHandler(name, field);
		}
	}
	
	
	public ShortArrayFieldHandler(String name, Field field) {
		super(name, field);
	}

	@Override
	public boolean isItemValid(Object array, int index) {
		return true; // always valid
	}

	@Override
	public void composeItem(Object array, int index, ComposingScope scope) 
			throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
		scope.append(Short.toString(Array.getShort(array, index)));
	}
	

	@Override
	public ParsingScope openScope(Object object) {
		
		return new ArrayScope() {
			private List<Short> values = new ArrayList<>();
			
			@Override
			public ParsingScope openItemScope() throws JOOS_ParsingException {
				return new StringScope() {
					@Override
					public void setValue(String value) throws JOOS_ParsingException, ParsingException {
						values.add(Short.valueOf(value));
					}
				};
			}

			@Override
			public void close() throws JOOS_ParsingException {
				int length = values.size();
				short[] array = new short[length];
				for(int i=0; i<length; i++) {
					array[i] = values.get(i);
				}
				try {
					ShortArrayFieldHandler.this.set(object, array);
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					throw new JOOS_ParsingException("Failed to set object due to "+e.getMessage());
				}
			}
		};
	}
}
