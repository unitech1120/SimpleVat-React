import { combineReducers } from 'redux'

import {
  AuthReducer,
  CommonReducer
} from './global'

import {
  Dashboard,
  BankAccount,
  BankStatement,
  Employee,
  Contact,
  Expense,
  GeneralSettings,
  CustomerInvoice,
  Receipt,
  SupplierInvoice,
  Product,
  Project,
  Payment,
  TransactionCategory,
  User,
  VatCategory,
  Currency,
  Help,
  Notification,
  OrganizationProfile,
  UsersRoles,
  DataBackup
} from 'screens'


const reducer = combineReducers({
  common: CommonReducer,
  auth: AuthReducer,

  dashboard: Dashboard.reducer,
  bank_account: BankAccount.reducer,
  bank_statement: BankStatement.reducer,
  employee: Employee.reducer,
  contact: Contact.reducer,
  expense: Expense.reducer,
  settings: GeneralSettings.reducer,
  customer_invoice: CustomerInvoice.reducer,
  receipt: Receipt.reducer,
  supplier_invoice: SupplierInvoice.reducer,
  product: Product.reducer,
  project: Project.reducer,
  payment: Payment.reducer,
  transaction: TransactionCategory.reducer,
  user: User.reducer,
  vat: VatCategory.reducer,
  currency: Currency.reducer,
  help: Help.reducer,
  notification: Notification.reducer,
  organization_profile: OrganizationProfile.reducer,
  users_roles: UsersRoles.reducer,
  data_backup: DataBackup.reducer
})

export default reducer