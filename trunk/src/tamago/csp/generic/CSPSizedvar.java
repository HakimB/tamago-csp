/**
 * 
 */
package tamago.csp.generic;

/**
 * This interface represent the all CSPvar, where we can express the length of the domain.
 * Usefull for the finite domain, that cannot be considered as FDvar. For example enum type
 * 
 * 
 * @author Hakim Belhaouari
 *
 */
public interface CSPSizedvar extends CSPvar {
	long size();
}
