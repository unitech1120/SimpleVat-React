package com.simplevat.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simplevat.dao.AbstractDao;
import com.simplevat.dao.ExpenseDao;
import com.simplevat.entity.Expense;

@Repository
@Transactional
public class ExpenseDaoImpl extends AbstractDao<Integer, Expense> implements ExpenseDao {

	@Override
	public List<Expense> getAllExpenses() {
		List<Expense> expenses = this.executeNamedQuery("allExpenses");
		return expenses;
	}

	@Override
	public List<Object[]> getExpensePerMonth(Date startDate, Date endDate) {
		List<Object[]> expenses = new ArrayList<>(0);
		try {
			String queryString = "select "
					+ "sum(e.expenseAmount) as expenseTotal, "
					+ "CONCAT(MONTH(e.expenseDate),'-' , Year(e.expenseDate)) as month "
					+ "from Expense e "
					+ "where e.deleteFlag = 'false' "
					+ "and e.expenseDate BETWEEN :startDate AND :endDate "
					+ "group by CONCAT(MONTH(e.expenseDate),'-' , Year(e.expenseDate))";

			Query query = getEntityManager().createQuery(queryString)
					.setParameter("startDate", startDate, TemporalType.DATE)
					.setParameter("endDate", endDate, TemporalType.DATE);
			expenses = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return expenses;
	}


	@Override
	public List<Object[]> getExpenses(Date startDate, Date endDate) {
		List<Object[]> expenses = new ArrayList<>(0);
		try {
			String queryString = "select "
					+ "e.expenseAmount as expense, e.expenseDate as date, "
					+ "e.receiptNumber as ref "
					+ "from Expense e "
					+ "where e.deleteFlag = 'false' "
					+ "and e.expenseDate BETWEEN :startDate AND :endDate "
					+ "order by e.expenseDate asc";

			Query query = getEntityManager().createQuery(queryString)
					.setParameter("startDate", startDate, TemporalType.DATE)
					.setParameter("endDate", endDate, TemporalType.DATE);
			expenses = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return expenses;
	}
	@Override
	public List<Object[]> getVatOutPerMonthWise(Date startDate, Date endDate) {
		List<Object[]> invoices = new ArrayList<>(0);
		try {
			String queryString = "select "
					+ "sum(i.expenseAmount*0.05) as vatOutTotal, "
					+ "CONCAT(MONTH(i.expenseDate),'-' , Year(i.expenseDate)) as month "
					+ "from Expense i "
					+ "where i.deleteFlag = 'false' "
					+ "and i.expenseDate BETWEEN :startDate AND :endDate "
					+ "group by CONCAT(MONTH(i.expenseDate),'-' , Year(i.expenseDate))";

			Query query = getEntityManager().createQuery(queryString)
					.setParameter("startDate", startDate, TemporalType.DATE)
					.setParameter("endDate", endDate, TemporalType.DATE);
			invoices = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return invoices;
	}



}
