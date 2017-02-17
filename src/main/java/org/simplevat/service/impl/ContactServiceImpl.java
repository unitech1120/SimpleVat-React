/**
 * 
 */
package org.simplevat.service.impl;

import java.util.List;

import org.simplevat.dao.ContactDao;
import org.simplevat.domain.user.Contact;
import org.simplevat.service.ContactService;

/**
 * @author MohsinH
 *
 */
public class ContactServiceImpl implements ContactService {
	
	
	public ContactDao contactDao;

	/* (non-Javadoc)
	 * @see org.simplevat.service.ContactService#createContact(org.simplevat.domain.user.Contact)
	 */
	@Override
	public Contact createContact(Contact contact) {
		
		return contactDao.createContact(contact);
	}

	/* (non-Javadoc)
	 * @see org.simplevat.service.ContactService#editContact(org.simplevat.domain.user.Contact)
	 */
	@Override
	public Contact editContact(Contact contact) {
		
		return contactDao.editContact(contact);
	}

	/* (non-Javadoc)
	 * @see org.simplevat.service.ContactService#deleteContact(org.simplevat.domain.user.Contact)
	 */
	@Override
	public void deleteContact(Contact contact) {
		contactDao.deleteContact(contact);

	}
	
	/* (non-Javadoc)
	 * @see org.simplevat.service.ContactService#getContacts()
	 */
	@Override
	public List<Contact> getContacts() {
		
		return contactDao.getContacts();
	}

	/* (non-Javadoc)
	 * @see org.simplevat.service.ContactService#getContactbyId()
	 */
	@Override
	public Contact getContactbyId(Integer contactId) {
		
		return contactDao.getContactbyId(contactId);
	}

	/**
	 * @return the contactDao
	 */
	public ContactDao getContactDao() {
		return contactDao;
	}

	/**
	 * @param contactDao the contactDao to set
	 */
	public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}


}
