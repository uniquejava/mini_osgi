package com.yipsilon.osgi.internal; 
 
import java.lang.reflect.Constructor; 
import java.lang.reflect.InvocationTargetException; 
import java.security.AccessController; 
import java.security.PrivilegedAction; 
import java.util.Map; 
import java.util.Vector; 
 
import com.yipsilon.osgi.IBundleFilter; 
import com.yipsilon.osgi.InvalidSyntaxException; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
public class BundleFilter implements IBundleFilter { 
 
  /** 
   * Constructs a {@link BundleFilter} object. This filter object may be used to match a Dictionary. 
   * <p> 
   * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be thrown with a human readable message 
   * where the filter became unparsable. 
   *  
   * @param filter 
   *            the filter string. 
   * @exception InvalidSyntaxException 
   *                If the filter parameter contains an invalid filter string that cannot be parsed. 
   */ 
  public BundleFilter(String filter) throws InvalidSyntaxException { 
    topLevel = true; 
    new Parser(filter).parse(this); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleFilter#match(java.util.Map) 
   */ 
  public boolean match(Map<String, Object> dictionary) { 
    return match0(dictionary); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleFilter#matchCase(java.util.Map) 
   */ 
  public boolean matchCase(Map<String, Object> dictionary) { 
    return match0(dictionary); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#toString() 
   */ 
  public String toString() { 
    if (this.filter == null) { 
      StringBuffer filter = new StringBuffer(); 
      filter.append('('); 
 
      switch (operation) { 
        case AND: { 
          filter.append('&'); 
 
          BundleFilter[] filters = (BundleFilter[]) value; 
          int size = filters.length; 
 
          for (int i = 0; i < size; i++) { 
            filter.append(filters[i].toString()); 
          } 
 
          break; 
        } 
 
        case OR: { 
          filter.append('|'); 
 
          BundleFilter[] filters = (BundleFilter[]) value; 
          int size = filters.length; 
 
          for (int i = 0; i < size; i++) { 
            filter.append(filters[i].toString()); 
          } 
 
          break; 
        } 
 
        case NOT: { 
          filter.append('!'); 
          filter.append(value.toString()); 
 
          break; 
        } 
 
        case SUBSTRING: { 
          filter.append(attr); 
          filter.append('='); 
 
          String[] substrings = (String[]) value; 
 
          int size = substrings.length; 
 
          for (int i = 0; i < size; i++) { 
            String substr = substrings[i]; 
 
            if (substr == null) /* * */{ 
              filter.append('*'); 
            } else /* xxx */{ 
              filter.append(encodeValue(substr)); 
            } 
          } 
 
          break; 
        } 
        case EQUAL: { 
          filter.append(attr); 
          filter.append('='); 
          filter.append(encodeValue(value.toString())); 
 
          break; 
        } 
        case GREATER: { 
          filter.append(attr); 
          filter.append(">="); //$NON-NLS-1$ 
          filter.append(encodeValue(value.toString())); 
 
          break; 
        } 
        case LESS: { 
          filter.append(attr); 
          filter.append("<="); //$NON-NLS-1$ 
          filter.append(encodeValue(value.toString())); 
 
          break; 
        } 
        case APPROX: { 
          filter.append(attr); 
          filter.append("~="); //$NON-NLS-1$ 
          filter.append(encodeValue(approxString(value.toString()))); 
 
          break; 
        } 
 
        case PRESENT: { 
          filter.append(attr); 
          filter.append("=*"); //$NON-NLS-1$ 
 
          break; 
        } 
      } 
 
      filter.append(')'); 
 
      if (topLevel) /* only hold onto String object at toplevel */{ 
        this.filter = filter.toString(); 
      } else { 
        return filter.toString(); 
      } 
    } 
 
    return this.filter; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#equals(java.lang.Object) 
   */ 
  public boolean equals(Object obj) { 
    if (obj == this) { 
      return true; 
    } 
 
    if (!(obj instanceof BundleFilter)) { 
      return false; 
    } 
 
    return this.toString().equals(obj.toString()); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#hashCode() 
   */ 
  public int hashCode() { 
    return this.toString().hashCode(); 
  } 
 
  /* Protected fields and methods for the Filter implementation */ 
 
  /** filter operation */ 
  protected int operation; 
 
  protected static final int EQUAL = 1; 
 
  protected static final int APPROX = 2; 
 
  protected static final int GREATER = 3; 
 
  protected static final int LESS = 4; 
 
  protected static final int PRESENT = 5; 
 
  protected static final int SUBSTRING = 6; 
 
  protected static final int AND = 7; 
 
  protected static final int OR = 8; 
 
  protected static final int NOT = 9; 
 
  /** filter attribute or null if operation AND, OR or NOT */ 
  protected String attr; 
 
  /** filter operands */ 
  protected Object value; 
 
  /* normalized filter string for topLevel Filter object */ 
  protected String filter; 
 
  /* true if root Filter object */ 
  protected boolean topLevel; 
 
  protected BundleFilter() { 
    topLevel = false; 
  } 
 
  protected void setFilter(int operation, String attr, Object value) { 
    this.operation = operation; 
    this.attr = attr; 
    this.value = value; 
  } 
 
  /** 
   * Internal match routine. Dictionary parameter must support case-insensitive get. 
   *  
   * @param properties 
   *            A dictionary whose keys are used in the match. 
   * @return If the Dictionary's keys match the filter, return <code>true</code>. Otherwise, return 
   *         <code>false</code>. 
   */ 
  protected boolean match0(Map<String, Object> properties) { 
    switch (operation) { 
      case AND: { 
        BundleFilter[] filters = (BundleFilter[]) value; 
        int size = filters.length; 
 
        for (int i = 0; i < size; i++) { 
          if (!filters[i].match0(properties)) { 
            return false; 
          } 
        } 
 
        return true; 
      } 
 
      case OR: { 
        BundleFilter[] filters = (BundleFilter[]) value; 
        int size = filters.length; 
 
        for (int i = 0; i < size; i++) { 
          if (filters[i].match0(properties)) { 
            return true; 
          } 
        } 
 
        return false; 
      } 
 
      case NOT: { 
        BundleFilter filter = (BundleFilter) value; 
 
        return !filter.match0(properties); 
      } 
 
      case SUBSTRING: 
      case EQUAL: 
      case GREATER: 
      case LESS: 
      case APPROX: { 
        Object prop = (properties == null) ? null : properties.get(attr); 
 
        return compare(operation, prop, value); 
      } 
 
      case PRESENT: { 
 
        Object prop = (properties == null) ? null : properties.get(attr); 
 
        return prop != null; 
      } 
    } 
 
    return false; 
  } 
 
  /** 
   * Encode the value string such that '(', '*', ')' and '\' are escaped. 
   *  
   * @param value 
   *            unencoded value string. 
   * @return encoded value string. 
   */ 
  protected static String encodeValue(String value) { 
    boolean encoded = false; 
    int inlen = value.length(); 
    int outlen = inlen << 1; /* inlen * 2 */ 
 
    char[] output = new char[outlen]; 
    value.getChars(0, inlen, output, inlen); 
 
    int cursor = 0; 
    for (int i = inlen; i < outlen; i++) { 
      char c = output[i]; 
 
      switch (c) { 
        case '(': 
        case '*': 
        case ')': 
        case '\\': { 
          output[cursor] = '\\'; 
          cursor++; 
          encoded = true; 
 
          break; 
        } 
      } 
 
      output[cursor] = c; 
      cursor++; 
    } 
 
    return encoded ? new String(output, 0, cursor) : value; 
  } 
 
  @SuppressWarnings("unchecked") 
  protected boolean compare(int operation, Object value1, Object value2) { 
    if (value1 == null) { 
 
      return false; 
    } 
 
    if (value1 instanceof String) { 
      return compare_String(operation, (String) value1, value2); 
    } 
 
    Class<?> clazz = value1.getClass(); 
 
    if (clazz.isArray()) { 
      Class<?> type = clazz.getComponentType(); 
 
      if (type.isPrimitive()) { 
        return compare_PrimitiveArray(operation, type, value1, value2); 
      } else { 
        return compare_ObjectArray(operation, (Object[]) value1, value2); 
      } 
    } 
 
    if (value1 instanceof Vector) { 
      return compare_Vector(operation, (Vector<?>) value1, value2); 
    } 
 
    if (value1 instanceof Integer) { 
      return compare_Integer(operation, ((Integer) value1).intValue(), value2); 
    } 
 
    if (value1 instanceof Long) { 
      return compare_Long(operation, ((Long) value1).longValue(), value2); 
    } 
 
    if (value1 instanceof Byte) { 
      return compare_Byte(operation, ((Byte) value1).byteValue(), value2); 
    } 
 
    if (value1 instanceof Short) { 
      return compare_Short(operation, ((Short) value1).shortValue(), value2); 
    } 
 
    if (value1 instanceof Character) { 
      return compare_Character(operation, ((Character) value1).charValue(), value2); 
    } 
 
    if (value1 instanceof Float) { 
      return compare_Float(operation, ((Float) value1).floatValue(), value2); 
    } 
 
    if (value1 instanceof Double) { 
      return compare_Double(operation, ((Double) value1).doubleValue(), value2); 
    } 
 
    if (value1 instanceof Boolean) { 
      return compare_Boolean(operation, ((Boolean) value1).booleanValue(), value2); 
    } 
 
    if (value1 instanceof Comparable) { 
      return compare_Comparable(operation, (Comparable<Object>) value1, value2); 
    } 
 
    return compare_Unknown(operation, value1, value2); // RFC 59 
  } 
 
  protected boolean compare_Vector(int operation, Vector<?> vector, Object value2) { 
    int size = vector.size(); 
 
    for (int i = 0; i < size; i++) { 
      if (compare(operation, vector.elementAt(i), value2)) { 
        return true; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_ObjectArray(int operation, Object[] array, Object value2) { 
    int size = array.length; 
 
    for (int i = 0; i < size; i++) { 
      if (compare(operation, array[i], value2)) { 
        return true; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_PrimitiveArray(int operation, Class<?> type, Object primarray, Object value2) { 
    if (Integer.TYPE.isAssignableFrom(type)) { 
      int[] array = (int[]) primarray; 
 
      int size = array.length; 
 
      for (int i = 0; i < size; i++) { 
        if (compare_Integer(operation, array[i], value2)) { 
          return true; 
        } 
      } 
 
      return false; 
    } 
 
    if (Long.TYPE.isAssignableFrom(type)) { 
      long[] array = (long[]) primarray; 
 
      int size = array.length; 
 
      for (int i = 0; i < size; i++) { 
        if (compare_Long(operation, array[i], value2)) { 
          return true; 
        } 
      } 
 
      return false; 
    } 
 
    if (Byte.TYPE.isAssignableFrom(type)) { 
      byte[] array = (byte[]) primarray; 
 
      int size = array.length; 
 
      for (int i = 0; i < size; i++) { 
        if (compare_Byte(operation, array[i], value2)) { 
          return true; 
        } 
      } 
 
      return false; 
    } 
 
    if (Short.TYPE.isAssignableFrom(type)) { 
      short[] array = (short[]) primarray; 
 
      int size = array.length; 
 
      for (int i = 0; i < size; i++) { 
        if (compare_Short(operation, array[i], value2)) { 
          return true; 
        } 
      } 
 
      return false; 
    } 
 
    if (Character.TYPE.isAssignableFrom(type)) { 
      char[] array = (char[]) primarray; 
 
      int size = array.length; 
 
      for (int i = 0; i < size; i++) { 
        if (compare_Character(operation, array[i], value2)) { 
          return true; 
        } 
      } 
 
      return false; 
    } 
 
    if (Float.TYPE.isAssignableFrom(type)) { 
      float[] array = (float[]) primarray; 
 
      int size = array.length; 
 
      for (int i = 0; i < size; i++) { 
        if (compare_Float(operation, array[i], value2)) { 
          return true; 
        } 
      } 
 
      return false; 
    } 
 
    if (Double.TYPE.isAssignableFrom(type)) { 
      double[] array = (double[]) primarray; 
 
      int size = array.length; 
 
      for (int i = 0; i < size; i++) { 
        if (compare_Double(operation, array[i], value2)) { 
          return true; 
        } 
      } 
 
      return false; 
    } 
 
    if (Boolean.TYPE.isAssignableFrom(type)) { 
      boolean[] array = (boolean[]) primarray; 
 
      int size = array.length; 
 
      for (int i = 0; i < size; i++) { 
        if (compare_Boolean(operation, array[i], value2)) { 
          return true; 
        } 
      } 
 
      return false; 
    } 
 
    return false; 
  } 
 
  protected boolean compare_String(int operation, String string, Object value2) { 
    switch (operation) { 
      case SUBSTRING: { 
 
        String[] substrings = (String[]) value2; 
        int pos = 0; 
 
        int size = substrings.length; 
 
        for (int i = 0; i < size; i++) { 
          String substr = substrings[i]; 
 
          if (i + 1 < size) /* if this is not that last substr */{ 
            if (substr == null) /* * */{ 
              String substr2 = substrings[i + 1]; 
 
              if (substr2 == null) /* ** */ 
                continue; /* ignore first star */ 
              /* *xxx */ 
              int index = string.indexOf(substr2, pos); 
              if (index == -1) { 
                return false; 
              } 
 
              pos = index + substr2.length(); 
              if (i + 2 < size) // if there are more substrings, increment over the string we just matched; otherwise 
                // need to do the last substr check 
                i++; 
            } else /* xxx */{ 
              int len = substr.length(); 
 
              if (string.regionMatches(pos, substr, 0, len)) { 
                pos += len; 
              } else { 
                return false; 
              } 
            } 
          } else /* last substr */{ 
            if (substr == null) /* * */{ 
              return true; 
            } else /* xxx */{ 
              return string.endsWith(substr); 
            } 
          } 
        } 
 
        return true; 
      } 
      case EQUAL: { 
        return string.equals(value2); 
      } 
      case APPROX: { 
        string = approxString(string); 
        String string2 = approxString((String) value2); 
 
        return string.equalsIgnoreCase(string2); 
      } 
      case GREATER: { 
        return string.compareTo((String) value2) >= 0; 
      } 
      case LESS: { 
        return string.compareTo((String) value2) <= 0; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Integer(int operation, int intval, Object value2) { 
    int intval2 = Integer.parseInt(((String) value2).trim()); 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return intval == intval2; 
      } 
      case APPROX: { 
        return intval == intval2; 
      } 
      case GREATER: { 
        return intval >= intval2; 
      } 
      case LESS: { 
        return intval <= intval2; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Long(int operation, long longval, Object value2) { 
    long longval2 = Long.parseLong(((String) value2).trim()); 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return longval == longval2; 
      } 
      case APPROX: { 
        return longval == longval2; 
      } 
      case GREATER: { 
        return longval >= longval2; 
      } 
      case LESS: { 
        return longval <= longval2; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Byte(int operation, byte byteval, Object value2) { 
    byte byteval2 = Byte.parseByte(((String) value2).trim()); 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return byteval == byteval2; 
      } 
      case APPROX: { 
        return byteval == byteval2; 
      } 
      case GREATER: { 
        return byteval >= byteval2; 
      } 
      case LESS: { 
        return byteval <= byteval2; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Short(int operation, short shortval, Object value2) { 
    short shortval2 = Short.parseShort(((String) value2).trim()); 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return shortval == shortval2; 
      } 
      case APPROX: { 
        return shortval == shortval2; 
      } 
      case GREATER: { 
        return shortval >= shortval2; 
      } 
      case LESS: { 
        return shortval <= shortval2; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Character(int operation, char charval, Object value2) { 
    char charval2 = (((String) value2).trim()).charAt(0); 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return charval == charval2; 
      } 
      case APPROX: { 
        return Character.toLowerCase(charval) == Character.toLowerCase(charval2); 
      } 
      case GREATER: { 
        return charval >= charval2; 
      } 
      case LESS: { 
        return charval <= charval2; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Boolean(int operation, boolean boolval, Object value2) { 
    boolean boolval2 = new Boolean(((String) value2).trim()).booleanValue(); 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return boolval == boolval2; 
      } 
      case APPROX: { 
        return boolval == boolval2; 
      } 
      case GREATER: { 
        return boolval == boolval2; 
      } 
      case LESS: { 
        return boolval == boolval2; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Float(int operation, float floatval, Object value2) { 
    float floatval2 = Float.parseFloat(((String) value2).trim()); 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return floatval == floatval2; 
      } 
      case APPROX: { 
        return floatval == floatval2; 
      } 
      case GREATER: { 
        return floatval >= floatval2; 
      } 
      case LESS: { 
        return floatval <= floatval2; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Double(int operation, double doubleval, Object value2) { 
    double doubleval2 = Double.parseDouble(((String) value2).trim()); 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return doubleval == doubleval2; 
      } 
      case APPROX: { 
        return doubleval == doubleval2; 
      } 
      case GREATER: { 
        return doubleval >= doubleval2; 
      } 
      case LESS: { 
        return doubleval <= doubleval2; 
      } 
    } 
 
    return false; 
  } 
 
  protected static final Class<?>[] constructorType = new Class<?>[] { String.class }; 
 
  protected boolean compare_Comparable(int operation, Comparable<Object> value1, Object value2) { 
    Constructor<?> constructor; 
 
    try { 
      constructor = value1.getClass().getConstructor(constructorType); 
    } catch (NoSuchMethodException e) { 
      return false; 
    } 
    try { 
      if (!constructor.isAccessible()) 
        AccessController.doPrivileged(new SetAccessibleAction(constructor)); 
      value2 = constructor.newInstance(new Object[] { ((String) value2).trim() }); 
    } catch (IllegalAccessException e) { 
      return false; 
    } catch (InvocationTargetException e) { 
      return false; 
    } catch (InstantiationException e) { 
      return false; 
    } 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return value1.compareTo(value2) == 0; 
      } 
      case APPROX: { 
        return value1.compareTo(value2) == 0; 
      } 
      case GREATER: { 
        return value1.compareTo(value2) >= 0; 
      } 
      case LESS: { 
        return value1.compareTo(value2) <= 0; 
      } 
    } 
 
    return false; 
  } 
 
  protected boolean compare_Unknown(int operation, Object value1, Object value2) { // RFC 59 
    Constructor<?> constructor; 
    try { 
      constructor = value1.getClass().getConstructor(constructorType); 
    } catch (NoSuchMethodException e) { 
      return false; 
    } 
    try { 
      if (!constructor.isAccessible()) 
        AccessController.doPrivileged(new SetAccessibleAction(constructor)); 
      value2 = constructor.newInstance(new Object[] { ((String) value2).trim() }); 
    } catch (IllegalAccessException e) { 
      return false; 
    } catch (InvocationTargetException e) { 
      return false; 
    } catch (InstantiationException e) { 
      return false; 
    } 
 
    switch (operation) { 
      case SUBSTRING: { 
        return false; 
      } 
      case EQUAL: { 
        return value1.equals(value2); 
      } 
      case APPROX: { 
        return value1.equals(value2); 
      } 
      case GREATER: { 
        return value1.equals(value2); 
      } 
      case LESS: { 
        return value1.equals(value2); 
      } 
    } 
 
    return false; 
  } 
 
  /** 
   * Map a string for an APPROX (~=) comparison. This implementation removes white spaces. This is the minimum 
   * implementation allowed by the OSGi spec. 
   *  
   * @param input 
   *            Input string. 
   * @return String ready for APPROX comparison. 
   */ 
  protected static String approxString(String input) { 
    boolean changed = false; 
    char[] output = input.toCharArray(); 
 
    int length = output.length; 
 
    int cursor = 0; 
    for (int i = 0; i < length; i++) { 
      char c = output[i]; 
 
      if (Character.isWhitespace(c)) { 
        changed = true; 
        continue; 
      } 
 
      output[cursor] = c; 
      cursor++; 
    } 
 
    return changed ? new String(output, 0, cursor) : input; 
  } 
 
  /** 
   * Parser class for OSGi filter strings. This class parses the complete filter string and builds a tree of Filter 
   * objects rooted at the parent. 
   */ 
  private static class Parser { 
    protected String filterstring; 
 
    protected char[] filter; 
 
    protected int pos; 
 
    protected Parser(String filterstring) { 
      this.filterstring = filterstring; 
      filter = filterstring.toCharArray(); 
      pos = 0; 
    } 
 
    protected void parse(BundleFilter parent) throws InvalidSyntaxException { 
      try { 
        parse_filter(parent); 
      } catch (ArrayIndexOutOfBoundsException e) { 
        throw new InvalidSyntaxException("Filter terminated abrubtly: " + filterstring); 
      } 
 
      if (pos != filter.length) { 
        throw new InvalidSyntaxException("Filter trailing characters: " + filterstring); 
      } 
    } 
 
    protected void parse_filter(BundleFilter parent) throws InvalidSyntaxException { 
      skipWhiteSpace(); 
 
      if (filter[pos] != '(') { 
        throw new InvalidSyntaxException("Filter missing left paren: " + filterstring); 
      } 
 
      pos++; 
 
      parse_filtercomp(parent); 
 
      skipWhiteSpace(); 
 
      if (filter[pos] != ')') { 
        throw new InvalidSyntaxException("Filter missing right paren: " + filterstring); 
      } 
 
      pos++; 
 
      skipWhiteSpace(); 
    } 
 
    protected void parse_filtercomp(BundleFilter parent) throws InvalidSyntaxException { 
      skipWhiteSpace(); 
 
      char c = filter[pos]; 
 
      switch (c) { 
        case '&': { 
          pos++; 
          parse_and(parent); 
          break; 
        } 
        case '|': { 
          pos++; 
          parse_or(parent); 
          break; 
        } 
        case '!': { 
          pos++; 
          parse_not(parent); 
          break; 
        } 
        default: { 
          parse_item(parent); 
          break; 
        } 
      } 
    } 
 
    protected void parse_and(BundleFilter parent) throws InvalidSyntaxException { 
      skipWhiteSpace(); 
 
      if (filter[pos] != '(') { 
        throw new InvalidSyntaxException("Filter missing left paren: " + filterstring); 
      } 
 
      Vector<BundleFilter> operands = new Vector<BundleFilter>(10, 10); 
 
      while (filter[pos] == '(') { 
        BundleFilter child = new BundleFilter(); 
        parse_filter(child); 
        operands.addElement(child); 
      } 
 
      int size = operands.size(); 
 
      BundleFilter[] children = new BundleFilter[size]; 
 
      operands.copyInto(children); 
 
      parent.setFilter(BundleFilter.AND, null, children); 
    } 
 
    protected void parse_or(BundleFilter parent) throws InvalidSyntaxException { 
      skipWhiteSpace(); 
 
      if (filter[pos] != '(') { 
        throw new InvalidSyntaxException("Filter missing left paren: " + filterstring); 
      } 
 
      Vector<BundleFilter> operands = new Vector<BundleFilter>(10, 10); 
 
      while (filter[pos] == '(') { 
        BundleFilter child = new BundleFilter(); 
        parse_filter(child); 
        operands.addElement(child); 
      } 
 
      int size = operands.size(); 
 
      BundleFilter[] children = new BundleFilter[size]; 
 
      operands.copyInto(children); 
 
      parent.setFilter(BundleFilter.OR, null, children); 
    } 
 
    protected void parse_not(BundleFilter parent) throws InvalidSyntaxException { 
      skipWhiteSpace(); 
 
      if (filter[pos] != '(') { 
        throw new InvalidSyntaxException("Filter missing left paren: " + filterstring); 
      } 
 
      BundleFilter child = new BundleFilter(); 
      parse_filter(child); 
 
      parent.setFilter(BundleFilter.NOT, null, child); 
    } 
 
    protected void parse_item(BundleFilter parent) throws InvalidSyntaxException { 
      String attr = parse_attr(); 
 
      skipWhiteSpace(); 
 
      switch (filter[pos]) { 
        case '~': { 
          if (filter[pos + 1] == '=') { 
            pos += 2; 
            parent.setFilter(BundleFilter.APPROX, attr, parse_value()); 
            return; 
          } 
          break; 
        } 
        case '>': { 
          if (filter[pos + 1] == '=') { 
            pos += 2; 
            parent.setFilter(BundleFilter.GREATER, attr, parse_value()); 
            return; 
          } 
          break; 
        } 
        case '<': { 
          if (filter[pos + 1] == '=') { 
            pos += 2; 
            parent.setFilter(BundleFilter.LESS, attr, parse_value()); 
            return; 
          } 
          break; 
        } 
        case '=': { 
          if (filter[pos + 1] == '*') { 
            int oldpos = pos; 
            pos += 2; 
            skipWhiteSpace(); 
            if (filter[pos] == ')') { 
              parent.setFilter(BundleFilter.PRESENT, attr, null); 
              return; /* present */ 
            } 
            pos = oldpos; 
          } 
 
          pos++; 
          Object string = parse_substring(); 
 
          if (string instanceof String) { 
            parent.setFilter(BundleFilter.EQUAL, attr, string); 
          } else { 
            parent.setFilter(BundleFilter.SUBSTRING, attr, string); 
          } 
 
          return; 
        } 
      } 
 
      throw new InvalidSyntaxException("Filter invalid operator: " + filterstring); 
    } 
 
    protected String parse_attr() throws InvalidSyntaxException { 
      skipWhiteSpace(); 
 
      int begin = pos; 
      int end = pos; 
 
      char c = filter[pos]; 
 
      while ("~<>=()".indexOf(c) == -1) { //$NON-NLS-1$ 
        pos++; 
 
        if (!Character.isWhitespace(c)) { 
          end = pos; 
        } 
 
        c = filter[pos]; 
      } 
 
      int length = end - begin; 
 
      if (length == 0) { 
        throw new InvalidSyntaxException("Filter missing attribute: " + filterstring); 
      } 
 
      return new String(filter, begin, length); 
    } 
 
    protected String parse_value() throws InvalidSyntaxException { 
      StringBuffer sb = new StringBuffer(filter.length - pos); 
 
      parseloop: while (true) { 
        char c = filter[pos]; 
 
        switch (c) { 
          case ')': { 
            break parseloop; 
          } 
 
          case '(': { 
            throw new InvalidSyntaxException("Filter invalid value: " + filterstring); 
          } 
 
          case '\\': { 
            pos++; 
            c = filter[pos]; 
            /* fall through into default */ 
          } 
 
          default: { 
            sb.append(c); 
            pos++; 
            break; 
          } 
        } 
      } 
 
      if (sb.length() == 0) { 
        throw new InvalidSyntaxException("Filter missing value: " + filterstring); 
      } 
 
      return sb.toString(); 
    } 
 
    protected Object parse_substring() throws InvalidSyntaxException { 
      StringBuffer sb = new StringBuffer(filter.length - pos); 
 
      Vector<String> operands = new Vector<String>(10, 10); 
 
      parseloop: while (true) { 
        char c = filter[pos]; 
 
        switch (c) { 
          case ')': { 
            if (sb.length() > 0) { 
              operands.addElement(sb.toString()); 
            } 
 
            break parseloop; 
          } 
 
          case '(': { 
            throw new InvalidSyntaxException("Filter invalid value: " + filterstring); 
          } 
 
          case '*': { 
            if (sb.length() > 0) { 
              operands.addElement(sb.toString()); 
            } 
 
            sb.setLength(0); 
 
            operands.addElement(null); 
            pos++; 
 
            break; 
          } 
 
          case '\\': { 
            pos++; 
            c = filter[pos]; 
            /* fall through into default */ 
          } 
 
          default: { 
            sb.append(c); 
            pos++; 
            break; 
          } 
        } 
      } 
 
      int size = operands.size(); 
 
      if (size == 0) { 
        throw new InvalidSyntaxException("Filter missing: " + filterstring); 
      } 
 
      if (size == 1) { 
        Object single = operands.elementAt(0); 
 
        if (single != null) { 
          return single; 
        } 
      } 
 
      String[] strings = new String[size]; 
 
      operands.copyInto(strings); 
 
      return strings; 
    } 
 
    protected void skipWhiteSpace() { 
      int length = filter.length; 
 
      while ((pos < length) && Character.isWhitespace(filter[pos])) { 
        pos++; 
      } 
    } 
  } 
 
  private static class SetAccessibleAction implements PrivilegedAction<Object> { 
    private Constructor<?> constructor; 
 
    public SetAccessibleAction(Constructor<?> constructor) { 
      this.constructor = constructor; 
    } 
 
    public Object run() { 
      constructor.setAccessible(true); 
      return null; 
    } 
  } 
 
}