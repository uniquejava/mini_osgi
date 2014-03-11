package com.yipsilon.osgi.internal; 
 
import java.util.NoSuchElementException; 
import java.util.StringTokenizer; 
 
import com.yipsilon.osgi.IBundleVersion; 
 
/** 
 * Version identifier for bundles and packages. 
 * <p> 
 * Version identifiers have four components. 
 * <ol> 
 * <li>Major version. A non-negative integer.</li> 
 * <li>Minor version. A non-negative integer.</li> 
 * <li>Micro version. A non-negative integer.</li> 
 * <li>Qualifier. A text string. See <code>BundleVersion(String)</code> for the format of the qualifier string.</li> 
 * </ol> 
 * <p> 
 * <code>BundleVersion</code> objects are immutable. 
 *  
 * @author yipsilon 
 * @since 1.0 
 */ 
class BundleVersion implements IBundleVersion { 
 
  private final int major; 
 
  private final int minor; 
 
  private final int micro; 
 
  private final String qualifier; 
 
  private static final String SEPARATOR = "."; //$NON-NLS-1$ 
 
  /** 
   * The empty version "0.0.0". Equivalent to calling <code>new Version(0,0,0)</code>. 
   */ 
  public static final BundleVersion EMPTY_VERSION = new BundleVersion(0, 0, 0); 
 
  /** 
   * Creates a version identifier from the specified numerical components. 
   * <p> 
   * The qualifier is set to the empty string. 
   *  
   * @param major 
   *            Major component of the version identifier. 
   * @param minor 
   *            Minor component of the version identifier. 
   * @param micro 
   *            Micro component of the version identifier. 
   * @throws IllegalArgumentException 
   *             If the numerical components are negative. 
   */ 
  public BundleVersion(int major, int minor, int micro) { 
    this(major, minor, micro, null); 
  } 
 
  /** 
   * Creates a version identifier from the specifed components. 
   *  
   * @param major 
   *            Major component of the version identifier. 
   * @param minor 
   *            Minor component of the version identifier. 
   * @param micro 
   *            Micro component of the version identifier. 
   * @param qualifier 
   *            Qualifier component of the version identifier. If <code>null</code> is specified, then the qualifier 
   *            will be set to the empty string. 
   * @throws IllegalArgumentException 
   *             If the numerical components are negative or the qualifier string is invalid. 
   */ 
  public BundleVersion(int major, int minor, int micro, String qualifier) { 
    if (qualifier == null) { 
      qualifier = ""; //$NON-NLS-1$ 
    } 
 
    this.major = major; 
    this.minor = minor; 
    this.micro = micro; 
    this.qualifier = qualifier; 
    validate(); 
  } 
 
  /** 
   * Created a version identifier from the specified string. 
   * <p> 
   * Here is the grammar for version strings. 
   *  
   * <pre> 
   *        version ::= major('.'minor('.'micro('.'qualifier)?)?)? 
   *        major ::= digit+ 
   *        minor ::= digit+ 
   *        micro ::= digit+ 
   *        qualifier ::= (alpha|digit|'_'|'-')+ 
   *        digit ::= [0..9] 
   *        alpha ::= [a..zA..Z] 
   * </pre> 
   *  
   * There must be no whitespace in version. 
   *  
   * @param version 
   *            String representation of the version identifier. 
   * @throws IllegalArgumentException 
   *             If <code>BundleVersion</code> is improperly formatted. 
   */ 
  public BundleVersion(String version) { 
    int major = 0; 
    int minor = 0; 
    int micro = 0; 
    String qualifier = ""; //$NON-NLS-1$ 
 
    try { 
      StringTokenizer st = new StringTokenizer(version, SEPARATOR, true); 
      major = Integer.parseInt(st.nextToken()); 
 
      if (st.hasMoreTokens()) { 
        st.nextToken(); // consume delimiter 
        minor = Integer.parseInt(st.nextToken()); 
 
        if (st.hasMoreTokens()) { 
          st.nextToken(); // consume delimiter 
          micro = Integer.parseInt(st.nextToken()); 
 
          if (st.hasMoreTokens()) { 
            st.nextToken(); // consume delimiter 
            qualifier = st.nextToken(); 
 
            if (st.hasMoreTokens()) { 
              throw new IllegalArgumentException("invalid format"); //$NON-NLS-1$ 
            } 
          } 
        } 
      } 
    } catch (NoSuchElementException e) { 
      throw new IllegalArgumentException("invalid format"); //$NON-NLS-1$ 
    } 
 
    this.major = major; 
    this.minor = minor; 
    this.micro = micro; 
    this.qualifier = qualifier; 
    validate(); 
  } 
 
  /** 
   * Called by the Version constructors to validate the version components. 
   *  
   * @throws IllegalArgumentException 
   *             If the numerical components are negative or the qualifier string is invalid. 
   */ 
  private void validate() { 
    if (major < 0) { 
      throw new IllegalArgumentException("negative major"); //$NON-NLS-1$ 
    } 
    if (minor < 0) { 
      throw new IllegalArgumentException("negative minor"); //$NON-NLS-1$ 
    } 
    if (micro < 0) { 
      throw new IllegalArgumentException("negative micro"); //$NON-NLS-1$ 
    } 
    int length = qualifier.length(); 
    for (int i = 0; i < length; i++) { 
      if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".indexOf(qualifier.charAt(i)) == -1) { //$NON-NLS-1$ 
        throw new IllegalArgumentException("invalid qualifier"); //$NON-NLS-1$ 
      } 
    } 
  } 
 
  /** 
   * Parses a version identifier from the specified string. 
   * <p> 
   * See <code>BundleVersion(String)</code> for the format of the version string. 
   *  
   * @param version 
   *            String representation of the version identifier. Leading and trailing whitespace will be ignored. 
   * @return A <code>BundleVersion</code> object representing the version identifier. If <code>BundleVersion</code> 
   *         is <code>null</code> or the empty string then <code>emptyVersion</code> will be returned. 
   * @throws IllegalArgumentException 
   *             If <code>BundleVersion</code> is improperly formatted. 
   */ 
  public static BundleVersion parseVersion(String version) { 
    if (version == null) { 
      return EMPTY_VERSION; 
    } 
 
    version = version.trim(); 
    if (version.length() == 0) { 
      return EMPTY_VERSION; 
    } 
 
    return new BundleVersion(version); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IBundleVersion#getMajor() 
   */ 
  public int getMajor() { 
    return major; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IBundleVersion#getMinor() 
   */ 
  public int getMinor() { 
    return minor; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IBundleVersion#getMicro() 
   */ 
  public int getMicro() { 
    return micro; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IBundleVersion#getQualifier() 
   */ 
  public String getQualifier() { 
    return qualifier; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#toString() 
   */ 
  public String toString() { 
    String base = major + SEPARATOR + minor + SEPARATOR + micro; 
    if (qualifier.length() == 0) { //$NON-NLS-1$ 
      return base; 
    } else { 
      return base + SEPARATOR + qualifier; 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#hashCode() 
   */ 
  public int hashCode() { 
    return (major << 24) + (minor << 16) + (micro << 8) + qualifier.hashCode(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#equals(java.lang.Object) 
   */ 
  public boolean equals(Object object) { 
    if (object == this) { // quicktest 
      return true; 
    } 
 
    if (!(object instanceof BundleVersion)) { 
      return false; 
    } 
 
    BundleVersion other = (BundleVersion) object; 
    return (major == other.major) && (minor == other.minor) && (micro == other.micro) && qualifier.equals(other.qualifier); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleVersion#compareTo(com.yipsilon.osgi.IBundleVersion) 
   */ 
  public int compareTo(IBundleVersion object) { 
    if (object == this) { // quicktest 
      return 0; 
    } 
 
    BundleVersion other = (BundleVersion) object; 
 
    int result = major - other.major; 
    if (result != 0) { 
      return result; 
    } 
 
    result = minor - other.minor; 
    if (result != 0) { 
      return result; 
    } 
 
    result = micro - other.micro; 
    if (result != 0) { 
      return result; 
    } 
 
    return qualifier.compareTo(other.qualifier); 
  } 
} 