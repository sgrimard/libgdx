// line 1 "JsonReader.rl"
// Do not edit this file! Generated by Ragel.
// Ragel.exe -G2 -J -o JsonReader.java JsonReader.rl
/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue.ValueType;

/** Lightweight JSON parser.<br>
 * <br>
 * The default behavior is to parse the JSON into a DOM containing {@link JsonValue} objects. Extend this class and override
 * methods to perform event driven parsing. When this is done, the parse methods will return null.
 * @author Nathan Sweet */
public class JsonReader implements BaseJsonReader {
	public JsonValue parse (String json) {
		char[] data = json.toCharArray();
		return parse(data, 0, data.length);
	}

	public JsonValue parse (Reader reader) {
		try {
			char[] data = new char[1024];
			int offset = 0;
			while (true) {
				int length = reader.read(data, offset, data.length - offset);
				if (length == -1) break;
				if (length == 0) {
					char[] newData = new char[data.length * 2];
					System.arraycopy(data, 0, newData, 0, data.length);
					data = newData;
				} else
					offset += length;
			}
			return parse(data, 0, offset);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			StreamUtils.closeQuietly(reader);
		}
	}

	public JsonValue parse (InputStream input) {
		try {
			return parse(new InputStreamReader(input, "UTF-8"));
		} catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			StreamUtils.closeQuietly(input);
		}
	}

	public JsonValue parse (FileHandle file) {
		try {
			return parse(file.reader("UTF-8"));
		} catch (Exception ex) {
			throw new SerializationException("Error parsing file: " + file, ex);
		}
	}

	public JsonValue parse (char[] data, int offset, int length) {
		int cs, p = offset, pe = length, eof = pe, top = 0;
		int[] stack = new int[4];

		int s = 0;
		Array<String> names = new Array(8);
		boolean needsUnescape = false, stringIsName = false, stringIsUnquoted = false;
		RuntimeException parseRuntimeEx = null;

		boolean debug = false;
		if (debug) System.out.println();

		try {

			// line 3 "JsonReader.java"
			{
				cs = json_start;
				top = 0;
			}

			// line 8 "JsonReader.java"
			{
				int _klen;
				int _trans = 0;
				int _acts;
				int _nacts;
				int _keys;
				int _goto_targ = 0;

				_goto:
				while (true) {
					switch (_goto_targ) {
					case 0:
						if (p == pe) {
							_goto_targ = 4;
							continue _goto;
						}
						if (cs == 0) {
							_goto_targ = 5;
							continue _goto;
						}
					case 1:
						_match:
						do {
							_keys = _json_key_offsets[cs];
							_trans = _json_index_offsets[cs];
							_klen = _json_single_lengths[cs];
							if (_klen > 0) {
								int _lower = _keys;
								int _mid;
								int _upper = _keys + _klen - 1;
								while (true) {
									if (_upper < _lower) break;

									_mid = _lower + ((_upper - _lower) >> 1);
									if (data[p] < _json_trans_keys[_mid])
										_upper = _mid - 1;
									else if (data[p] > _json_trans_keys[_mid])
										_lower = _mid + 1;
									else {
										_trans += (_mid - _keys);
										break _match;
									}
								}
								_keys += _klen;
								_trans += _klen;
							}

							_klen = _json_range_lengths[cs];
							if (_klen > 0) {
								int _lower = _keys;
								int _mid;
								int _upper = _keys + (_klen << 1) - 2;
								while (true) {
									if (_upper < _lower) break;

									_mid = _lower + (((_upper - _lower) >> 1) & ~1);
									if (data[p] < _json_trans_keys[_mid])
										_upper = _mid - 2;
									else if (data[p] > _json_trans_keys[_mid + 1])
										_lower = _mid + 2;
									else {
										_trans += ((_mid - _keys) >> 1);
										break _match;
									}
								}
								_trans += _klen;
							}
						} while (false);

						_trans = _json_indicies[_trans];
						cs = _json_trans_targs[_trans];

						if (_json_trans_actions[_trans] != 0) {
							_acts = _json_trans_actions[_trans];
							_nacts = (int)_json_actions[_acts++];
							while (_nacts-- > 0) {
								switch (_json_actions[_acts++]) {
								case 0:
								// line 108 "JsonReader.rl"
								{
									stringIsName = true;
								}
									break;
								case 1:
								// line 111 "JsonReader.rl"
								{
									String value = new String(data, s, p - s);
									if (needsUnescape) value = unescape(value);
									outer:
									if (stringIsName) {
										stringIsName = false;
										if (debug) System.out.println("name: " + value);
										names.add(value);
									} else {
										String name = names.size > 0 ? names.pop() : null;
										if (stringIsUnquoted) {
											if (value.equals("true")) {
												if (debug) System.out.println("boolean: " + name + "=true");
												bool(name, true);
												break outer;
											} else if (value.equals("false")) {
												if (debug) System.out.println("boolean: " + name + "=false");
												bool(name, false);
												break outer;
											} else if (value.equals("null")) {
												string(name, null);
												break outer;
											}
											boolean couldBeDouble = false, couldBeLong = true;
											outer2:
											for (int i = s; i < p; i++) {
												switch (data[i]) {
												case '0':
												case '1':
												case '2':
												case '3':
												case '4':
												case '5':
												case '6':
												case '7':
												case '8':
												case '9':
												case '-':
												case '+':
													break;
												case '.':
												case 'e':
												case 'E':
													couldBeDouble = true;
													couldBeLong = false;
													break;
												default:
													couldBeDouble = false;
													couldBeLong = false;
													break outer2;
												}
											}
											if (couldBeDouble) {
												try {
													if (debug) System.out.println("double: " + name + "=" + Double.parseDouble(value));
													number(name, Double.parseDouble(value));
													break outer;
												} catch (NumberFormatException ignored) {
												}
											} else if (couldBeLong) {
												if (debug) System.out.println("double: " + name + "=" + Double.parseDouble(value));
												try {
													number(name, Long.parseLong(value));
													break outer;
												} catch (NumberFormatException ignored) {
												}
											}
										}
										if (debug) System.out.println("string: " + name + "=" + value);
										string(name, value);
									}
									stringIsUnquoted = false;
									s = p;
								}
									break;
								case 2:
								// line 153 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("startObject: " + name);
									startObject(name);
									{
										if (top == stack.length) {
											int[] newStack = new int[stack.length * 2];
											System.arraycopy(stack, 0, newStack, 0, stack.length);
											stack = newStack;
										}
										{
											stack[top++] = cs;
											cs = 5;
											_goto_targ = 2;
											if (true) continue _goto;
										}
									}
								}
									break;
								case 3:
								// line 159 "JsonReader.rl"
								{
									if (debug) System.out.println("endObject");
									pop();
									{
										cs = stack[--top];
										_goto_targ = 2;
										if (true) continue _goto;
									}
								}
									break;
								case 4:
								// line 164 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("startArray: " + name);
									startArray(name);
									{
										if (top == stack.length) {
											int[] newStack = new int[stack.length * 2];
											System.arraycopy(stack, 0, newStack, 0, stack.length);
											stack = newStack;
										}
										{
											stack[top++] = cs;
											cs = 19;
											_goto_targ = 2;
											if (true) continue _goto;
										}
									}
								}
									break;
								case 5:
								// line 170 "JsonReader.rl"
								{
									if (debug) System.out.println("endArray");
									pop();
									{
										cs = stack[--top];
										_goto_targ = 2;
										if (true) continue _goto;
									}
								}
									break;
								case 6:
								// line 175 "JsonReader.rl"
								{
									if (debug) System.out.println("comment /" + data[p]);
									if (data[p++] == '/') {
										while (data[p] != '\n')
											p++;
									} else {
										while (data[p] != '*' || data[p + 1] != '/')
											p++;
										p++;
									}
								}
									break;
								case 7:
								// line 186 "JsonReader.rl"
								{
									if (debug) System.out.println("unquotedChars");
									s = p;
									needsUnescape = false;
									stringIsUnquoted = true;
									if (stringIsName) {
										outer:
										while (true) {
											switch (data[p]) {
											case '\\':
												needsUnescape = true;
												break;
											case ':':
											case ' ':
											case '\r':
											case '\n':
											case '\t':
												break outer;
											}
											// if (debug) System.out.println("unquotedChar (name): '" + data[p] + "'");
											p++;
											if (p == eof) break;
										}
									} else {
										outer:
										while (true) {
											switch (data[p]) {
											case '\\':
												needsUnescape = true;
												break;
											case '}':
											case ']':
											case ',':
											case ' ':
											case '\r':
											case '\n':
											case '\t':
												break outer;
											}
											// if (debug) System.out.println("unquotedChar (value): '" + data[p] + "'");
											p++;
											if (p == eof) break;
										}
									}
									p--;
								}
									break;
								case 8:
								// line 232 "JsonReader.rl"
								{
									if (debug) System.out.println("quotedChars");
									s = ++p;
									needsUnescape = false;
									outer:
									while (true) {
										switch (data[p]) {
										case '\\':
											needsUnescape = true;
											p++;
											break;
										case '"':
											break outer;
										}
										// if (debug) System.out.println("quotedChar: '" + data[p] + "'");
										p++;
										if (p == eof) break;
									}
									p--;
								}
									break;
								// line 271 "JsonReader.java"
								}
							}
						}

					case 2:
						if (cs == 0) {
							_goto_targ = 5;
							continue _goto;
						}
						if (++p != pe) {
							_goto_targ = 1;
							continue _goto;
						}
					case 4:
						if (p == eof) {
							int __acts = _json_eof_actions[cs];
							int __nacts = (int)_json_actions[__acts++];
							while (__nacts-- > 0) {
								switch (_json_actions[__acts++]) {
								case 1:
								// line 111 "JsonReader.rl"
								{
									String value = new String(data, s, p - s);
									s = p;
									if (needsUnescape) value = unescape(value);
									outer:
									if (stringIsName) {
										stringIsName = false;
										if (debug) System.out.println("name: " + value);
										names.add(value);
									} else {
										String name = names.size > 0 ? names.pop() : null;
										if (stringIsUnquoted) {
											if (value.equals("true")) {
												if (debug) System.out.println("boolean: " + name + "=true");
												bool(name, true);
												break outer;
											} else if (value.equals("false")) {
												if (debug) System.out.println("boolean: " + name + "=false");
												bool(name, false);
												break outer;
											} else if (value.equals("null")) {
												string(name, null);
												break outer;
											} else if (value.indexOf('.') != -1) {
												try {
													if (debug) System.out.println("double: " + name + "=" + Double.parseDouble(value));
													number(name, Double.parseDouble(value));
													break outer;
												} catch (NumberFormatException ignored) {
												}
											} else {
												try {
													if (debug) System.out.println("double: " + name + "=" + Double.parseDouble(value));
													number(name, Long.parseLong(value));
													break outer;
												} catch (NumberFormatException ignored) {
												}
											}
										}
										if (debug) System.out.println("string: " + name + "=" + value);
										string(name, value);
									}
									stringIsUnquoted = false;
								}
									break;
								// line 337 "JsonReader.java"
								}
							}
						}

					case 5:
					}
					break;
				}
			}

			// line 263 "JsonReader.rl"

		} catch (RuntimeException ex) {
			parseRuntimeEx = ex;
		}

		JsonValue root = this.root;
		this.root = null;
		current = null;
		lastChild.clear();

		if (p < pe) {
			int lineNumber = 1;
			for (int i = 0; i < p; i++)
				if (data[i] == '\n') lineNumber++;
			throw new SerializationException("Error parsing JSON on line " + lineNumber + " near: " + new String(data, p, pe - p),
				parseRuntimeEx);
		} else if (elements.size != 0) {
			JsonValue element = elements.peek();
			elements.clear();
			if (element != null && element.isObject())
				throw new SerializationException("Error parsing JSON, unmatched brace.");
			else
				throw new SerializationException("Error parsing JSON, unmatched bracket.");
		} else if (parseRuntimeEx != null) {
			throw new SerializationException("Error parsing JSON: " + new String(data), parseRuntimeEx);
		}
		return root;
	}

	// line 347 "JsonReader.java"
	private static byte[] init__json_actions_0 () {
		return new byte[] {0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 1, 8, 2, 0, 7, 2, 0, 8, 2, 1, 3, 2, 1, 5};
	}

	private static final byte _json_actions[] = init__json_actions_0();

	private static short[] init__json_key_offsets_0 () {
		return new short[] {0, 0, 12, 14, 15, 17, 29, 35, 41, 43, 55, 62, 69, 81, 82, 84, 86, 87, 89, 91, 103, 110, 117, 129, 130,
			132, 134, 136, 141, 146, 146};
	}

	private static final short _json_key_offsets[] = init__json_key_offsets_0();

	private static char[] init__json_trans_keys_0 () {
		return new char[] {13, 32, 34, 44, 47, 58, 91, 93, 123, 125, 9, 10, 42, 47, 34, 42, 47, 13, 32, 34, 44, 47, 58, 91, 93,
			123, 125, 9, 10, 13, 32, 47, 58, 9, 10, 13, 32, 47, 58, 9, 10, 42, 47, 13, 32, 34, 44, 47, 58, 91, 93, 123, 125, 9, 10,
			13, 32, 44, 47, 125, 9, 10, 13, 32, 44, 47, 125, 9, 10, 13, 32, 34, 44, 47, 58, 91, 93, 123, 125, 9, 10, 34, 42, 47, 42,
			47, 34, 42, 47, 42, 47, 13, 32, 34, 44, 47, 58, 91, 93, 123, 125, 9, 10, 13, 32, 44, 47, 93, 9, 10, 13, 32, 44, 47, 93,
			9, 10, 13, 32, 34, 44, 47, 58, 91, 93, 123, 125, 9, 10, 34, 42, 47, 42, 47, 42, 47, 13, 32, 47, 9, 10, 13, 32, 47, 9,
			10, 0};
	}

	private static final char _json_trans_keys[] = init__json_trans_keys_0();

	private static byte[] init__json_single_lengths_0 () {
		return new byte[] {0, 10, 2, 1, 2, 10, 4, 4, 2, 10, 5, 5, 10, 1, 2, 2, 1, 2, 2, 10, 5, 5, 10, 1, 2, 2, 2, 3, 3, 0, 0};
	}

	private static final byte _json_single_lengths[] = init__json_single_lengths_0();

	private static byte[] init__json_range_lengths_0 () {
		return new byte[] {0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0};
	}

	private static final byte _json_range_lengths[] = init__json_range_lengths_0();

	private static short[] init__json_index_offsets_0 () {
		return new short[] {0, 0, 12, 15, 17, 20, 32, 38, 44, 47, 59, 66, 73, 85, 87, 90, 93, 95, 98, 101, 113, 120, 127, 139, 141,
			144, 147, 150, 155, 160, 161};
	}

	private static final short _json_index_offsets[] = init__json_index_offsets_0();

	private static byte[] init__json_indicies_0 () {
		return new byte[] {1, 1, 2, 3, 4, 3, 5, 3, 6, 3, 1, 0, 7, 7, 3, 8, 3, 9, 9, 3, 11, 11, 12, 13, 14, 3, 3, 3, 3, 15, 11, 10,
			16, 16, 17, 18, 16, 3, 19, 19, 20, 21, 19, 3, 22, 22, 3, 21, 21, 24, 3, 25, 3, 26, 3, 27, 3, 21, 23, 28, 28, 29, 30, 31,
			28, 3, 32, 32, 13, 33, 15, 32, 3, 13, 13, 12, 3, 34, 3, 3, 3, 3, 15, 13, 10, 16, 3, 35, 35, 3, 36, 36, 3, 28, 3, 37, 37,
			3, 38, 38, 3, 40, 40, 41, 42, 43, 3, 44, 45, 46, 3, 40, 39, 47, 47, 48, 49, 50, 47, 3, 51, 51, 42, 52, 45, 51, 3, 42,
			42, 41, 3, 53, 3, 44, 45, 46, 3, 42, 39, 47, 3, 54, 54, 3, 55, 55, 3, 56, 56, 3, 8, 8, 57, 8, 3, 58, 58, 59, 58, 3, 3,
			3, 0};
	}

	private static final byte _json_indicies[] = init__json_indicies_0();

	private static byte[] init__json_trans_targs_0 () {
		return new byte[] {27, 1, 3, 0, 4, 28, 28, 28, 28, 1, 6, 5, 13, 12, 18, 29, 7, 8, 9, 7, 8, 9, 7, 10, 16, 17, 11, 11, 11,
			12, 15, 29, 11, 15, 14, 12, 11, 9, 5, 20, 19, 23, 22, 26, 21, 30, 21, 21, 22, 25, 30, 21, 25, 24, 22, 21, 19, 2, 28, 2};
	}

	private static final byte _json_trans_targs[] = init__json_trans_targs_0();

	private static byte[] init__json_trans_actions_0 () {
		return new byte[] {13, 0, 15, 0, 0, 7, 3, 11, 1, 11, 17, 0, 20, 0, 0, 5, 1, 1, 1, 0, 0, 0, 11, 13, 15, 0, 7, 3, 1, 1, 1,
			23, 0, 0, 0, 11, 11, 11, 11, 13, 0, 15, 0, 0, 7, 9, 3, 1, 1, 1, 26, 0, 0, 0, 11, 11, 11, 1, 0, 0};
	}

	private static final byte _json_trans_actions[] = init__json_trans_actions_0();

	private static byte[] init__json_eof_actions_0 () {
		return new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0};
	}

	private static final byte _json_eof_actions[] = init__json_eof_actions_0();

	static final int json_start = 1;
	static final int json_first_final = 27;
	static final int json_error = 0;

	static final int json_en_object = 5;
	static final int json_en_array = 19;
	static final int json_en_main = 1;

	// line 293 "JsonReader.rl"

	private final Array<JsonValue> elements = new Array(8);
	private final Array<JsonValue> lastChild = new Array(8);
	private JsonValue root, current;

	private void addChild (String name, JsonValue child) {
		child.setName(name);
		if (current == null) {
			current = child;
			root = child;
		} else if (current.isArray() || current.isObject()) {
			if (current.size == 0)
				current.child = child;
			else {
				JsonValue last = lastChild.pop();
				last.next = child;
				child.prev = last;
			}
			lastChild.add(child);
			current.size++;
		} else
			root = current;
	}

	protected void startObject (String name) {
		JsonValue value = new JsonValue(ValueType.object);
		if (current != null) addChild(name, value);
		elements.add(value);
		current = value;
	}

	protected void startArray (String name) {
		JsonValue value = new JsonValue(ValueType.array);
		if (current != null) addChild(name, value);
		elements.add(value);
		current = value;
	}

	protected void pop () {
		root = elements.pop();
		if (current.size > 0) lastChild.pop();
		current = elements.size > 0 ? elements.peek() : null;
	}

	protected void string (String name, String value) {
		addChild(name, new JsonValue(value));
	}

	protected void number (String name, double value) {
		addChild(name, new JsonValue(value));
	}

	protected void number (String name, long value) {
		addChild(name, new JsonValue(value));
	}

	protected void bool (String name, boolean value) {
		addChild(name, new JsonValue(value));
	}

	private String unescape (String value) {
		int length = value.length();
		StringBuilder buffer = new StringBuilder(length + 16);
		for (int i = 0; i < length;) {
			char c = value.charAt(i++);
			if (c != '\\') {
				buffer.append(c);
				continue;
			}
			if (i == length) break;
			c = value.charAt(i++);
			if (c == 'u') {
				buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
				i += 4;
				continue;
			}
			switch (c) {
			case '"':
			case '\\':
			case '/':
				break;
			case 'b':
				c = '\b';
				break;
			case 'f':
				c = '\f';
				break;
			case 'n':
				c = '\n';
				break;
			case 'r':
				c = '\r';
				break;
			case 't':
				c = '\t';
				break;
			default:
				throw new SerializationException("Illegal escaped character: \\" + c);
			}
			buffer.append(c);
		}
		return buffer.toString();
	}
}
