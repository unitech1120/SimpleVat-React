package com.simplevat.web.bankaccount.controller;

import com.github.javaplugs.jsf.SpringScopeView;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.simplevat.web.bankaccount.model.TransactionModel;
import com.simplevat.entity.Purchase;
import com.simplevat.entity.bankaccount.BankAccount;
import com.simplevat.entity.bankaccount.Transaction;
import com.simplevat.entity.bankaccount.TransactionCategory;
import com.simplevat.entity.bankaccount.TransactionStatus;
import com.simplevat.entity.bankaccount.TransactionType;
import com.simplevat.entity.bankaccount.TransactionView;
import com.simplevat.entity.invoice.Invoice;
import com.simplevat.service.PurchaseService;
import com.simplevat.service.TransactionCategoryServiceNew;
import com.simplevat.service.bankaccount.BankAccountService;
import com.simplevat.service.bankaccount.TransactionService;
import com.simplevat.service.bankaccount.TransactionStatusService;
import com.simplevat.service.bankaccount.TransactionTypeService;
import com.simplevat.service.invoice.InvoiceService;
import com.simplevat.web.bankaccount.model.BankAccountModel;
import com.simplevat.web.bankaccount.model.ReferenceObjectClass;
import com.simplevat.web.bankaccount.model.TransactionViewModel;
import com.simplevat.web.constant.InvoicePurchaseStatusConstant;
import com.simplevat.web.constant.TransactionCategoryConsatant;
import com.simplevat.web.constant.TransactionCreditDebitConstant;
import com.simplevat.web.constant.TransactionEntryTypeConstant;
import com.simplevat.web.constant.TransactionRefrenceTypeConstant;
import com.simplevat.web.constant.TransactionStatusConstant;
import com.simplevat.web.utils.FacesUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

@Controller
@SpringScopeView
public class TransactionListControllerScndDemo extends TransactionControllerHelperDemo implements Serializable {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private TransactionCategoryServiceNew transactionCategoryService;
    @Autowired
    private TransactionStatusService transactionStatusService;
    @Autowired
    private TransactionTypeService transactionTypeService;
    @Autowired
    private BankAccountService bankAccountService;
    private List<TransactionViewModel> transactionViewModelList = new ArrayList<>();
    private List<TransactionModel> transactionModelList = new ArrayList<>();

    private List<Invoice> invoiceList = new ArrayList<>();

    @Getter
    @Setter
    private List<TransactionViewModel> childTransactions = new ArrayList<>();

    private List<TransactionStatus> transactionStatuseList = new ArrayList<>();

    @Getter
    private BankAccountModel selectedBankAccountModel;

    private Integer bankAccountId;

    @Getter
    @Setter
    private TransactionModel transactionModel;

    @Getter
    @Setter
    private TransactionViewModel selectedTransactionViewModel;

    @Getter
    @Setter
    private TransactionModel expandedTransactionModel;

    @Getter
    @Setter
    private TransactionModel childTransactionModel;

    @Getter
    @Setter
    private Transaction transaction;

    @Getter
    @Setter
    private boolean renderedInvoice;

    @Getter
    @Setter
    private int totalUnExplained;

    @Getter
    @Setter
    private int totalExplained;

    @Getter
    @Setter
    private int totalPartiallyExplained;

    @Getter
    @Setter
    private int totalTransactions;

    @Getter
    @Setter
    private TransactionViewModel selectedTransactionModel;
    private TransactionViewModel prevParentTransactionModel;
    private TransactionViewModel prevChildTransactionModel;
    private boolean expanded = false;
    int parentTransIndex;
    private int datatableInitialRowCount = 10;
    private int datatableRowCount = datatableInitialRowCount;
    TransactionViewModel currentTransactionModel;

    @PostConstruct
    public void init() {
        totalUnExplained = 0;
        totalExplained = 0;
        totalPartiallyExplained = 0;
        try {
            bankAccountId = FacesUtil.getSelectedBankAccountId();
            List<Transaction> transactionList = transactionService.getAllTransactionListByBankAccountId(bankAccountId);
            transactionStatuseList = transactionStatusService.findAllTransactionStatues();
            if (transactionList != null) {
                for (Transaction transaction : transactionList) {
                    transactionModelList.add(getTransactionModel(transaction));
                }
            }
            List<TransactionView> transactionViewList = transactionService.getAllTransactionViewList(bankAccountId);
            transactionViewModelList = new ArrayList<TransactionViewModel>();
            populateChildTransaction(transactionViewList);
            populateTransactionList(transactionViewList);
            transactionModel = new TransactionModel();
            expandedTransactionModel = new TransactionModel();
            invoiceList = invoiceService.getInvoiceListByDueAmount();
            if (invoiceList == null) {
                invoiceList = new ArrayList();
            }
        } catch (Exception ex) {
            Logger.getLogger(TransactionListControllerDemo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void populateChildTransaction(List<TransactionView> transactionList) {
        super.getChildTransactions().clear();
        if (transactionList != null) {
            for (TransactionView transactionView : transactionList) {
                if (transactionView.getParentTransaction() != null) {
                    TransactionViewModel transactionViewModel = this.getTransactionViewModel(transactionView);
                    if (transactionViewModel.getExplanationStatusCode() == TransactionStatusConstant.UNEXPLAINED
                            || transactionViewModel.getExplanationStatusCode() == TransactionStatusConstant.PARTIALLYEXPLIANED) {
                        getSuggestionStringForUnexplainedOrPartial(transactionViewModel);
                    } else {
                        getSuggestionStringForExplianed(transactionViewModel);
                    }
                    super.getChildTransactions().add(transactionViewModel);
                }
            }
        }
    }

    private void populateTransactionList(List<TransactionView> transactionList) {
        if (transactionList != null) {
            for (TransactionView transactionView : transactionList) {
                if (transactionView.getParentTransaction() == null) {
                    TransactionViewModel transactionViewModel = this.getTransactionViewModel(transactionView);
                    if (transactionView.getExplanationStatusCode() == TransactionStatusConstant.UNEXPLAINED) {
                        totalUnExplained++;
                    } else if (transactionView.getExplanationStatusCode() == TransactionStatusConstant.PARTIALLYEXPLIANED) {
                        totalPartiallyExplained++;
                    } else if (transactionView.getExplanationStatusCode() == TransactionStatusConstant.EXPLIANED) {
                        totalExplained++;
                    }
                    totalTransactions++;
                    if (transactionViewModel.getExplanationStatusCode() == TransactionStatusConstant.UNEXPLAINED
                            || transactionViewModel.getExplanationStatusCode() == TransactionStatusConstant.PARTIALLYEXPLIANED) {
                        getSuggestionStringForUnexplainedOrPartial(transactionViewModel);
                    } else {
                        getSuggestionStringForExplianed(transactionViewModel);
                    }
                    transactionViewModelList.add(transactionViewModel);
                }
            }
        }
    }

    private void getSuggestionStringForUnexplainedOrPartial(TransactionViewModel transactionViewModel) {
        String suggestedTransactionString = "";
        BigDecimal suggestionAmount = transactionViewModel.getTransactionAmount();
        if (!transactionViewModel.getChildTransactionList().isEmpty()) {
            int i = 0;
            for (TransactionViewModel childModel : transactionViewModel.getChildTransactionList()) {
                if (i == transactionViewModel.getChildTransactionList().size() - 1) {
                    break;
                }
                suggestionAmount = suggestionAmount.subtract(childModel.getTransactionAmount());
                i++;
            }
        }
        if (transactionViewModel.getDebitCreditFlag() == TransactionCreditDebitConstant.CREDIT) {
            if (invoiceList != null && !invoiceList.isEmpty()) {
                for (Invoice invoice : invoiceList) {
                    if (suggestionAmount.doubleValue() == invoice.getDueAmount().doubleValue()) {
                        transactionViewModel.setReferenceId(invoice.getInvoiceId());
                        suggestedTransactionString = transactionViewModel.getTransactionTypeName() + " : " + transactionViewModel.getTransactionCategoryName()
                                + " : Invoice Paid : " + invoice.getInvoiceReferenceNumber() + " : " + invoice.getInvoiceContact().getFirstName()
                                + " : " + invoice.getCurrency().getCurrencySymbol() + " " + invoice.getDueAmount()
                                + " : Due On : " + new SimpleDateFormat("MM/dd/yyyy").format(Date.from(invoice.getInvoiceDueDate().atZone(ZoneId.systemDefault()).toInstant()));
                        break;
                    }
                }
            }
        }
        transactionViewModel.setSuggestedTransactionString(suggestedTransactionString);
    }

    private void getSuggestionStringForExplianed(TransactionViewModel transactionViewModel) {
        String suggestedTransactionString = "";
        if (!transactionViewModel.getChildTransactionList().isEmpty()) {
            TransactionViewModel childTransactionViewModel = transactionViewModel.getChildTransactionList().get(0);
            suggestedTransactionString = generateSuggestedTransactionString(childTransactionViewModel);
        } else {
            suggestedTransactionString = generateSuggestedTransactionString(transactionViewModel);
        }
        transactionViewModel.setSuggestedTransactionString(suggestedTransactionString);
    }

    private String generateSuggestedTransactionString(TransactionViewModel transactionViewModel) {
        String transactionDecription = "";
        if (transactionViewModel.getTransactionDescription() != null) {
            String[] transactionDecrpStringArr = transactionViewModel.getTransactionDescription().split(" ");
            transactionDecription = transactionDecrpStringArr.length > 1 ? transactionDecrpStringArr[0] + " " + transactionDecrpStringArr[1] : transactionDecrpStringArr[0];
        }
        if (transactionViewModel.getReferenceId() != null) {
            return transactionViewModel.getTransactionTypeName() + " : " + transactionViewModel.getTransactionCategoryName()
                    + " : " + (transactionViewModel.getTransactionDescription() != null ? transactionDecription + " : " : "") + transactionViewModel.getReferenceName() + " : " + transactionViewModel.getContactName()
                    + " : " + transactionViewModel.getCurrencySymbol() + " " + transactionViewModel.getDueAmount()
                    + " : Due On : " + new SimpleDateFormat("MM/dd/yyyy").format(transactionViewModel.getDueOn())
                    + "......";

        } else {
            return transactionViewModel.getTransactionTypeName() + " : " + transactionViewModel.getTransactionCategoryName()
                    + " : " + (transactionViewModel.getTransactionDescription() != null ? transactionDecription : "") + "......";
        }
    }

    public void acceptSuggestion(TransactionModel transactionModel) {
        BigDecimal suggestionAmount = transactionModel.getTransactionAmount();
        if (!transactionModel.getChildTransactionList().isEmpty()) {
            int i = 0;
            for (TransactionModel childModel : transactionModel.getChildTransactionList()) {
                if (i == transactionModel.getChildTransactionList().size() - 1) {
                    break;
                }
                suggestionAmount = suggestionAmount.subtract(childModel.getTransactionAmount());
                i++;
            }
        }
        if (transactionModel.getTransactionAmount().doubleValue() > suggestionAmount.doubleValue()) {
            this.transactionModel = transactionModel.getChildTransactionList().get(transactionModel.getChildTransactionList().size() - 1);
        } else {
            this.transactionModel = transactionModel;
        }
        TransactionCategory category = transactionCategoryService.findByPK(TransactionCategoryConsatant.TRANSACTION_CATEGORY_INVOICE_PAYMENT);
        this.transactionModel.setTransactionType(category.getTransactionType());
        this.transactionModel.setExplainedTransactionCategory(category);
        this.transactionModel.setTransactionDescription("Invoice Paid");
        this.transactionModel.setRefObject(invoiceService.findByPK(transactionModel.getReferenceId()));
        this.transactionModel.setReferenceId(null);
        updateTransactionDropDown();
    }

    public void updateTransactionDropDown() {
        if (transactionModel.isParent()) {
            Transaction transaction = getTransactionEntity(transactionModel);
            saveUpdateDefineTransaction(transaction, null, getRefObject());
        } else {
            Transaction transaction = transactionModel.getParentTransaction();
            Transaction childTransaction = getTransactionEntity(transactionModel);
            saveUpdateDefineTransaction(transaction, childTransaction, getRefObject());
        }
        init();
    }

    private Object getRefObject() {
        Object refObject = null;
        if (transactionModel.getExplainedTransactionCategory().getTransactionCategoryId() == TransactionCategoryConsatant.TRANSACTION_CATEGORY_INVOICE_PAYMENT) {
            refObject = transactionModel.getRefObject();
        } else if (transactionModel.getExplainedTransactionCategory().getParentTransactionCategory() != null) {
            if (transactionModel.getExplainedTransactionCategory().getParentTransactionCategory().getTransactionCategoryId() == TransactionCategoryConsatant.TRANSACTION_CATEGORY_PURCHASE) {
                refObject = transactionModel.getRefObject();
            }
        }
        return refObject;
    }

    //Service UpdateMethod
    public void saveUpdateDefineTransaction(Transaction transaction, Transaction childTransaction, Object referanceObject) {
        /**
         * Update Ref object if it was attached
         */
        if (childTransaction != null) {
            updatePrevReference(childTransaction);
        } else {
            updatePrevReference(transaction);
        }
        BigDecimal transactionAmount = transaction.getTransactionAmount();
        transaction.setLastUpdateDate(LocalDateTime.now());
        transaction.setLastUpdateBy(FacesUtil.getLoggedInUser().getUserId());
        transaction.setTransactionStatus(transactionStatusService.findByPK(TransactionStatusConstant.EXPLIANED));
        BigDecimal explainedAmount = new BigDecimal(0);
        BigDecimal tempTransactionAmount = null;
        if (childTransaction != null) {
            transactionAmount = childTransaction.getTransactionAmount();
            if (childTransaction.getTransactionId() > 0) {
                for (Transaction tempChildTransaction : transaction.getChildTransactionList()) {
                    explainedAmount = explainedAmount.add(tempChildTransaction.getTransactionAmount());
                }
                tempTransactionAmount = transactionAmount.add(transaction.getTransactionAmount().subtract(explainedAmount));
                System.out.println("transactionAmount :" + transactionAmount);
                transactionAmount = tempTransactionAmount;
            }
        }
        if (referanceObject != null) {
            if (referanceObject instanceof Invoice) {
                Invoice invoice = (Invoice) referanceObject;
                invoice = invoiceService.findByPK(invoice.getInvoiceId());// refreshing the object
                referanceObject = invoice;
                updateInvoiceReference(transaction, childTransaction, transactionAmount, invoice);
            } else if (referanceObject instanceof Purchase) {
                Purchase purchase = (Purchase) referanceObject;
                purchase = purchaseService.findByPK(purchase.getPurchaseId());// refreshing the object
                referanceObject = purchase;
                updatePurchaseReference(transaction, childTransaction, transactionAmount, purchase);
            }
        } else {
            if (childTransaction == null) {
                transaction.setTransactionStatus(transactionStatusService.findByPK(TransactionStatusConstant.EXPLIANED));
            } else {
                transaction.setTransactionStatus(transactionStatusService.findByPK(TransactionStatusConstant.EXPLIANED));
                childTransaction.setTransactionStatus(transactionStatusService.findByPK(TransactionStatusConstant.EXPLIANED));
                childTransaction.setCurrentBalance(new BigDecimal(0.00));
                childTransaction.setTransactionDate(LocalDateTime.now());
                if (childTransaction.getTransactionId() == null || childTransaction.getTransactionId() == 0) {
                    childTransaction = createNewChildTransaction(childTransaction, transaction.getDebitCreditFlag(), childTransaction.getTransactionAmount());
                    childTransaction.setCreatedBy(FacesUtil.getLoggedInUser().getUserId());
                    childTransaction.setCreatedDate(LocalDateTime.now());
                    transaction.getChildTransactionList().add(childTransaction);
                } else {
                    childTransaction.setTransactionAmount(transactionAmount);
                    childTransaction.setLastUpdateBy(FacesUtil.getLoggedInUser().getUserId());
                    childTransaction.setLastUpdateDate(LocalDateTime.now());
                }
            }
        }

        if (childTransaction != null) {
            if (childTransaction.getTransactionId() != null && childTransaction.getTransactionId() > 0) {
                transaction.setVersionNumber(childTransaction.getParentTransaction().getVersionNumber());
                transaction.setChildTransactionList(null);
                if (childTransaction.getTransactionAmount().doubleValue() == transaction.getTransactionAmount().doubleValue()) {
                    transaction.setReferenceId(childTransaction.getReferenceId());
                    transaction.setReferenceType(childTransaction.getReferenceType());
                    transaction.setTransactionType(childTransaction.getTransactionType());
                    transaction.setExplainedTransactionCategory(childTransaction.getExplainedTransactionCategory());
                    transaction.setTransactionDescription(childTransaction.getTransactionDescription());
                    transactionService.deleteChildTransaction(childTransaction);
                } else {
                    childTransaction.setBankAccount(transaction.getBankAccount());
                    transactionService.persistChildTransaction(childTransaction);
                }
            }
        }
        transactionService.update(transaction);
        if (referanceObject != null) {
            if (referanceObject instanceof Invoice) {
                invoiceService.update((Invoice) referanceObject);
            } else if (referanceObject instanceof Purchase) {
                purchaseService.update((Purchase) referanceObject);
            }
        }
    }

    private void updateInvoiceReference(Transaction parentTransaction, Transaction childTransaction, BigDecimal transactionAmount, Invoice invoice) {
        BigDecimal invoiceDueAmount = invoice.getDueAmount();
        invoice.setLastUpdateDate(LocalDateTime.now());
        invoice.setLastUpdateBy(FacesUtil.getLoggedInUser().getUserId());
        ReferenceObjectClass referenceObjectClass = new ReferenceObjectClass(invoice.getInvoiceId(), invoice.getDueAmount(), TransactionRefrenceTypeConstant.INVOICE, invoice.getStatus());
        updateTransactionByRefrenceObject(parentTransaction, childTransaction, transactionAmount, invoiceDueAmount, referenceObjectClass);
        invoice.setDueAmount(referenceObjectClass.getDueAmount());
        invoice.setStatus(referenceObjectClass.getStatus());
    }

    private void updatePurchaseReference(Transaction parentTransaction, Transaction childTransaction, BigDecimal transactionAmount, Purchase purchase) {
        BigDecimal purchaseDueAmount = purchase.getPurchaseDueAmount();
        purchase.setLastUpdateDate(LocalDateTime.now());
        purchase.setLastUpdateBy(FacesUtil.getLoggedInUser().getUserId());
        ReferenceObjectClass referenceObjectClass = new ReferenceObjectClass(purchase.getPurchaseId(), purchase.getPurchaseDueAmount(), TransactionRefrenceTypeConstant.PURCHASE, purchase.getStatus());
        updateTransactionByRefrenceObject(parentTransaction, childTransaction, transactionAmount, purchaseDueAmount, referenceObjectClass);
        purchase.setPurchaseDueAmount(referenceObjectClass.getDueAmount());
        purchase.setStatus(referenceObjectClass.getStatus());
    }

    private void updateTransactionByRefrenceObject(Transaction parentTransaction, Transaction childTransaction, BigDecimal transactionAmount, BigDecimal prevDueAmount, ReferenceObjectClass referenceObject) {
        if (transactionAmount.doubleValue() <= prevDueAmount.doubleValue()) {
            referenceObject.setDueAmount(prevDueAmount.subtract(transactionAmount));
            referenceObject.setStatus(InvoicePurchaseStatusConstant.PAID);
            if (prevDueAmount.doubleValue() > transactionAmount.doubleValue()) {
                referenceObject.setStatus(InvoicePurchaseStatusConstant.PARTIALPAID);
            }
            if (childTransaction != null) {
                if (childTransaction.getTransactionId() == 0) {
                    childTransaction = createNewChildTransaction(childTransaction, parentTransaction.getDebitCreditFlag(), transactionAmount);
                } else {
                    childTransaction.setTransactionAmount(transactionAmount);
                    childTransaction.setLastUpdateBy(FacesUtil.getLoggedInUser().getUserId());
                    childTransaction.setLastUpdateDate(LocalDateTime.now());
                }
                childTransaction.setReferenceId(referenceObject.getId());
                childTransaction.setReferenceType(referenceObject.getReferenceType());
            } else {
                parentTransaction.setReferenceId(referenceObject.getId());
                parentTransaction.setReferenceType(referenceObject.getReferenceType());
            }
        } else {
            parentTransaction.setTransactionStatus(transactionStatusService.findByPK(TransactionStatusConstant.PARTIALLYEXPLIANED));
            Transaction newChildTransaction = null;
            if (childTransaction == null) {
                newChildTransaction = createNewChildTransaction(parentTransaction, parentTransaction.getDebitCreditFlag(), referenceObject.getDueAmount());
            } else if (childTransaction.getTransactionId() == 0) {
                newChildTransaction = createNewChildTransaction(childTransaction, parentTransaction.getDebitCreditFlag(), referenceObject.getDueAmount());
            } else {
                childTransaction.setReferenceId(referenceObject.getId());
                childTransaction.setReferenceType(referenceObject.getReferenceType());
                childTransaction.setTransactionStatus(transactionStatusService.findByPK(TransactionStatusConstant.EXPLIANED));
                childTransaction.setTransactionAmount(referenceObject.getDueAmount());
            }
            if (newChildTransaction != null) {
                newChildTransaction.setReferenceId(referenceObject.getId());
                newChildTransaction.setReferenceType(referenceObject.getReferenceType());
                parentTransaction.getChildTransactionList().add(newChildTransaction);
            }
            parentTransaction.setExplainedTransactionCategory(null);
            parentTransaction.setTransactionType(null);
            referenceObject.setDueAmount(new BigDecimal(0));
            referenceObject.setStatus(InvoicePurchaseStatusConstant.PAID);
        }
    }

    private void updatePrevReference(Transaction transaction) {
        Integer prevReferanceId = transaction.getReferenceId();
        if (prevReferanceId != null) {
            if (transaction.getReferenceType() == TransactionRefrenceTypeConstant.INVOICE) {
                Invoice invoice = invoiceService.findByPK(transaction.getReferenceId());
                invoice.setDueAmount(invoice.getDueAmount().add(transaction.getTransactionAmount()));
                invoiceService.update(invoice);
            } else if (transaction.getReferenceType() == TransactionRefrenceTypeConstant.PURCHASE) {
                Purchase purchase = purchaseService.findByPK(transaction.getReferenceId());
                purchase.setPurchaseDueAmount(purchase.getPurchaseDueAmount().add(transaction.getTransactionAmount()));
                purchaseService.update(purchase);
            }
            transaction.setReferenceId(null);
            transaction.setReferenceType(null);
        }
    }

    private Transaction createNewChildTransaction(Transaction transaction, char transactionCreditDebitConstant, BigDecimal transactionAmount) {
        Transaction childTransaction = new Transaction();
        childTransaction.setDebitCreditFlag(transactionCreditDebitConstant);
        childTransaction.setCreatedBy(FacesUtil.getLoggedInUser().getUserId());
        childTransaction.setCurrentBalance(new BigDecimal(0.00));
        childTransaction.setCreatedDate(LocalDateTime.now());
        if (transaction.getParentTransaction() == null) {
            childTransaction.setParentTransaction(transaction);
        } else {
            childTransaction.setParentTransaction(transaction.getParentTransaction());
        }
        childTransaction.setTransactionDate(childTransaction.getParentTransaction().getTransactionDate());
        childTransaction.setBankAccount(transaction.getParentTransaction().getBankAccount());
        childTransaction.setEntryType(TransactionEntryTypeConstant.SYSTEM);
        childTransaction.setTransactionType(transaction.getTransactionType());
        childTransaction.setTransactionStatus(transactionStatusService.findByPK(TransactionStatusConstant.EXPLIANED));
        childTransaction.setExplainedTransactionCategory(transaction.getExplainedTransactionCategory());
        childTransaction.setTransactionDescription(transaction.getTransactionDescription());
        childTransaction.setTransactionAmount(transactionAmount);
        return childTransaction;
    }

    public List<Invoice> completeInvoice() {
        return invoiceService.getInvoiceListByDueAmount();
    }

    public List<Purchase> completePurchase() {
        List<Purchase> purchaseList = purchaseService.getPurchaseListByDueAmount();
        return purchaseList;
    }

    public List<TransactionCategory> transactionCategories() throws Exception {
        List<TransactionCategory> transactionCategoryParentList = new ArrayList<>();
        List<TransactionCategory> transactionCategoryList = new ArrayList<>();
        if (expandedTransactionModel.getTransactionType() != null) {
            transactionCategoryList = transactionCategoryService.findAllTransactionCategoryByTransactionType(expandedTransactionModel.getTransactionType().getTransactionTypeCode());
        }
        System.out.println("transactionCategoryList" + transactionCategoryList);
        for (TransactionCategory transactionCategory : transactionCategoryList) {
            if (transactionCategory.getParentTransactionCategory() != null) {
                transactionCategoryParentList.add(transactionCategory.getParentTransactionCategory());
            }
        }
        transactionCategoryList.removeAll(transactionCategoryParentList);
        return transactionCategoryList;
    }

    public List<TransactionStatus> transactionStatuses(final String searchQuery) throws Exception {
        return transactionStatusService.executeNamedQuery("findAllTransactionStatues");
    }

    public List<TransactionType> transactionTypes(char debitCreditFlag) throws Exception {
        if (debitCreditFlag == TransactionCreditDebitConstant.CREDIT) {
            return transactionTypeService.executeNamedQuery("findMoneyInTransactionType");
        } else if (debitCreditFlag == TransactionCreditDebitConstant.DEBIT) {
            return transactionTypeService.executeNamedQuery("findMoneyOutTransactionType");
        }
        return new ArrayList<>();
    }

    public void expandCollapse(TransactionViewModel transactionViewModel) throws Exception {
        transactionViewModel.setExpandIcon(!transactionViewModel.isExpandIcon());
        if (transactionViewModel.isParent()) {
            for (TransactionViewModel transactionViewModel2 : transactionViewModelList) {
                if (transactionViewModel.getTransactionId() != transactionViewModel2.getTransactionId()) {
                    transactionViewModel2.setExpandIcon(false);
                    for (TransactionViewModel childTransactionViewModel : transactionViewModel2.getChildTransactionList()) {
                        childTransactionViewModel.setExpandIcon(false);
                    }
                }
            }
            for (TransactionViewModel childTransactionViewModel : transactionViewModel.getChildTransactionList()) {
                childTransactionViewModel.setExpandIcon(false);
            }
        } else {
            TransactionViewModel parentTransactionViewModel = new TransactionViewModel();
            for (TransactionViewModel transactionViewModel2 : transactionViewModelList) {
                if (transactionViewModel.getParentTransaction() == transactionViewModel2.getTransactionId()) {
                    parentTransactionViewModel = transactionViewModel2;
                }
            }
            for (TransactionViewModel childTransactionViewModel : parentTransactionViewModel.getChildTransactionList()) {
                if (transactionViewModel.getTransactionId() != childTransactionViewModel.getTransactionId()) {
                    childTransactionViewModel.setExpandIcon(false);
                }
            }
        }
    }

    public void refreshTableDataonPageChange() {
        List<TransactionViewModel> nextPageList = transactionViewModelList.subList(datatableRowCount, transactionViewModelList.size());
        System.out.println("transactionViewModelList" + transactionViewModelList.size());
        System.out.println("nextPageList" + nextPageList.size());
        int rowcount = 0;
        int parentcount = 0;
        for (TransactionViewModel transactionViewModel : nextPageList) {
            ++rowcount;
            if (transactionViewModel.isParent()) {
                ++parentcount;
                if (parentcount == 10) {
                    break;
                }
            }
        }
        for (TransactionViewModel transactionViewModel : nextPageList) {
            if (!transactionViewModel.isParent()) {
                if (nextPageList.get(rowcount).getTransactionId() == transactionViewModel.getParentTransaction()) {
                    ++rowcount;
                }
            }
        }
        System.out.println("rowcount" + rowcount);
        datatableRowCount = rowcount;
//        datatableRowCount = datatableInitialRowCount;
//        System.out.println("datatableRowCount ===================" + datatableRowCount);
//        if (prevParentTransactionModel != null && prevParentTransactionModel.isParent()) {
//            if (prevParentTransactionModel.getChildTransactionList() != null && !prevParentTransactionModel.getChildTransactionList().isEmpty()) {
//                for (TransactionViewModel transactionViewModel : prevParentTransactionModel.getChildTransactionList()) {
//                    transactionViewModelList.remove(transactionViewModel);
//                }
//            }
        for (TransactionViewModel viewModel : transactionViewModelList) {
            viewModel.setExpandIcon(false);
        }
        expanded = false;
//        }
    }

    private void displayTreeIcon() {
        if (expanded) {
            TransactionViewModel parentTransactionViewModel = currentTransactionModel;
            if (!parentTransactionViewModel.isParent()) {
                parentTransactionViewModel = prevParentTransactionModel;
            }
            int startIndex = transactionViewModelList.indexOf(parentTransactionViewModel);
            System.out.println("startIndex :" + startIndex);
            int childTransCount = parentTransactionViewModel.getChildTransactionList().size();
            if (childTransCount > 0) {
                RequestContext.getCurrentInstance().execute(" $('.ui-datatable-data').find('tr').eq(" + startIndex + ").addClass('first-child');");
                RequestContext.getCurrentInstance().execute(" $('.ui-datatable-data').find('tr').eq(" + startIndex + ").find('td').eq(0).addClass('first-child');");
                for (int i = 0; i < childTransCount; i++) {
                    RequestContext.getCurrentInstance().execute(" $('.ui-datatable-data').find('tr').eq(" + ++startIndex + ").find('td').eq(0).addClass('first-child');");
                }
                RequestContext.getCurrentInstance().execute(" $('.ui-datatable-data').find('tr').eq(" + startIndex + ").addClass('last-child');");
            }
        }
    }

    public void expandToggler(TransactionViewModel transactionViewModel) {
        expandedTransactionModel = new TransactionModel();
        if (transactionViewModel.getTransactionId() > 0) {
            for (TransactionModel transactionModel : transactionModelList) {
                if (transactionViewModel.getTransactionId() == transactionModel.getTransactionId()) {
                    System.out.println("time before" + new Date());
                    expandedTransactionModel = transactionModel;
                    System.out.println("time after" + new Date());
                }
            }
        } else {
            expandedTransactionModel.setTransactionId(transactionViewModel.getTransactionId());
            expandedTransactionModel.setDebitCreditFlag(transactionViewModel.getDebitCreditFlag());
            for (TransactionStatus transactionStatus : transactionStatuseList) {
                if (transactionStatus.getExplainationStatusCode() == TransactionStatusConstant.UNEXPLAINED) {
                    expandedTransactionModel.setTransactionStatus(transactionStatus);
                }
            }
            expandedTransactionModel.setTransactionAmount(transactionViewModel.getTransactionAmount());
            for (TransactionModel model : transactionModelList) {
                if (model.getParentTransaction() != null) {
                    if (transactionViewModel.getParentTransaction() == model.getParentTransaction().getTransactionId()) {
                        expandedTransactionModel.setParentTransaction(model.getParentTransaction());
                    }
                }
            }
        }
    }

    public String redirectToReference() {
        if (childTransactionModel.getReferenceType() == TransactionRefrenceTypeConstant.INVOICE) {
            return "/pages/secure/invoice/invoice.xhtml?faces-redirect=true&selectedInvoiceModelId=" + childTransactionModel.getReferenceId();
        }
        return null;
    }

    public void allUnExplainedTransactions() {
        transactionViewModelList.clear();
        for (TransactionView transactionView : transactionService.getAllTransactionViewList(bankAccountId)) {
            TransactionViewModel transactionViewModel = this.getTransactionViewModel(transactionView);
            if (transactionView.getExplanationStatusCode() == TransactionStatusConstant.UNEXPLAINED) {
                transactionViewModelList.add(transactionViewModel);
            }
        }
    }

    public void allExplainedTransactions() {
        transactionViewModelList.clear();
        for (TransactionView transactionView : transactionService.getAllTransactionViewList(bankAccountId)) {
            TransactionViewModel transactionViewModel = this.getTransactionViewModel(transactionView);
            if (transactionView.getExplanationStatusCode() == TransactionStatusConstant.EXPLIANED) {
                transactionViewModelList.add(transactionViewModel);
            }
        }
    }

    public void allPartiallyExplainedTransactions() {
        transactionViewModelList.clear();
        for (TransactionView transactionView : transactionService.getAllTransactionViewList(bankAccountId)) {
            TransactionViewModel transactionViewModel = this.getTransactionViewModel(transactionView);
            if (transactionView.getExplanationStatusCode() == TransactionStatusConstant.PARTIALLYEXPLIANED) {
                transactionViewModelList.add(transactionViewModel);
            }
        }
    }

    public List<TransactionViewModel> getTransactionViewModelList() throws Exception {
        return transactionViewModelList;
    }

    public void setTransactionViewModelList(List<TransactionViewModel> transactionViewModelList) {
        this.transactionViewModelList = transactionViewModelList;
    }

    public int getDatatableRowCount() {
        return datatableRowCount;
    }

    public void allTransactions() {
        for (TransactionView transactionView : transactionService.getAllTransactionViewList(bankAccountId)) {
            TransactionViewModel transactionViewModel = this.getTransactionViewModel(transactionView);
            if (transaction.getTransactionStatus().getExplainationStatusCode() == TransactionStatusConstant.UNEXPLAINED) {
                totalUnExplained++;
            } else if (transaction.getTransactionStatus().getExplainationStatusCode() == TransactionStatusConstant.PARTIALLYEXPLIANED) {
                totalPartiallyExplained++;
            } else if (transaction.getTransactionStatus().getExplainationStatusCode() == TransactionStatusConstant.EXPLIANED) {
                totalExplained++;
            }
            totalTransactions++;
            transactionViewModelList.add(transactionViewModel);
        }
    }

    public String editTransection() {
        System.out.println("selectedM :" + selectedTransactionModel);
        return "edit-bank-transaction?faces-redirect=true&selectedTransactionId=" + selectedTransactionViewModel.getTransactionId();
    }

    public String deleteTransaction() {
        Transaction transaction = new Transaction();
        for (TransactionModel model : transactionModelList) {
            if (model.getTransactionId() == selectedTransactionViewModel.getTransactionId()) {
                transaction = getTransactionEntity(model);
            }
        }
        transaction.setDeleteFlag(true);
        if (transaction.getParentTransaction() != null) {
            transactionService.deleteChildTransaction(transaction);
        } else {
            transactionService.deleteTransaction(transaction);
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Transaction deleted successfully"));
        return "bank-transactions?faces-redirect=true";
    }

}