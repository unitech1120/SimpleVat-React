/**
 * 
 */
package org.simplevat.dao;

import java.util.List;

import org.simplevat.domain.user.Contact;

/**
 * @author MohsinH
 *
 */
public interface ContactDao {
	
	public Contact createContact(Contact contact);
	
	public Contact editContact(Contact contact);
	
	public void deleteContact(Contact contact);
	
	public List<Contact> getContacts();
	
	public Contact getContactbyId(Integer contactId);
}
