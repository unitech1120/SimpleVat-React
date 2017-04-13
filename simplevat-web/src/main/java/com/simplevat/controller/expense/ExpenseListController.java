package com.simplevat.controller.expense;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.simplevat.entity.Expense;
import com.simplevat.expense.model.ExpenseModel;
import com.simplevat.service.ExpenseService;


@Controller
@ManagedBean(name = "expenseListController")
@RequestScoped
public class ExpenseListController extends ExpenseControllerHelper{
	
	@Autowired
	private ExpenseService expenseService;
	
    private List<ExpenseModel> expenses;
	
	public List<ExpenseModel> getExpenses() {
		
		List<Expense> expenseList = expenseService.getExpenses();
		
		expenses = new ArrayList<ExpenseModel>();
		
		for(Expense expense : expenseList){
			ExpenseModel model  = this.getExpenseModel(expense);
			expenses.add(model);
		}
		
		return expenses;
	}

	public void setExpenses(List<ExpenseModel> expenses) {
		this.expenses = expenses;
	}
	
	


}
