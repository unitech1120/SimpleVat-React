import React from 'react'
import {connect} from 'react-redux'
import { bindActionCreators } from 'redux'
import {
  Card,
  CardHeader,
  CardBody,
  Button,
  Modal,
  ModalHeader,
  ModalBody, 
  ModalFooter,
  Row,
  Col,
  Input,
  FormGroup,
  Form,
  ButtonGroup
} from 'reactstrap'
import { ToastContainer, toast } from 'react-toastify'
import { BootstrapTable, TableHeaderColumn, SearchField } from 'react-bootstrap-table'
import moment from 'moment'

import { Loader } from 'components'

import 'react-toastify/dist/ReactToastify.css'
import 'react-bootstrap-table/dist/react-bootstrap-table-all.min.css'
import './style.scss'

import * as VatActions from './actions'


const mapStateToProps = (state) => {
  return ({
    vat_list: state.vat.vat_list
  })
}
const mapDispatchToProps = (dispatch) => {
  return ({
    vatActions: bindActionCreators(VatActions, dispatch)
  })
}

class VatCode extends React.Component {
  constructor(props) {
    super(props)

    this.state = {
      openDeleteModal: false,
      loading: true
    }

    this.deleteVat = this.deleteVat.bind(this)
    this.customSearchField = this.customSearchField.bind(this)
    this.success = this.success.bind(this)
    this.customTotal = this.customTotal.bind(this)
    this.vatPercentageFormat = this.vatPercentageFormat.bind(this)

    this.closeModal = this.closeModal.bind(this)
    this.goToDetail = this.goToDetail.bind(this)

    this.options = {
      onRowClick: this.goToDetail,
      paginationPosition: 'top'
    }

    this.selectRowProp = {
      mode: 'checkbox',
      bgColor: 'rgba(0,0,0, 0.05)',
      onSelect: this.onRowSelect,
      onSelectAll: this.onSelectAll
    }
  }

  // Table Custom Search Field
  customSearchField(props) {
    return (
      <SearchField
        defaultValue=''
        placeholder='Search ...'/>
    )
  }

  // Table Custom Pagination Label
  customTotal(from, to, size) {
    return (
      <span className="react-bootstrap-table-pagination-total">
        Showing {from} to {to} of {size} Results
      </span >
    )
  }

  // -------------------------
  // Data Table Custom Fields
  //--------------------------
  
  vatPercentageFormat(cell, row) {
    return(`${row.vat} %`)
  }

  goToDetail (row) {
    this.props.history.push('/admin/master/vat-code/detail')
  }

  // Show Success Toast
  success() {
    return toast.success('Vat Code Deleted Successfully... ', {
        position: toast.POSITION.TOP_RIGHT
    })
  }

  
  componentDidMount() {
    this.getVatListData()
  }

  // Get All Vats
  getVatListData() {
    this.props.vatActions.getVatList().then(res => {
      if (res.status === 200) {
        this.setState({ loading: false })
      }
    })
  }

  // Delete Vat By ID
  deleteVat() {
    this.setState({ loading: true })
    this.setState({ openDeleteModal: false })
    this.props.vatActions.deleteVat(this.state.selectedData.id).then(res => {
      if (res.status === 200) {
        this.setState({ loading: false })
        this.getVatListData()
      }
    })
  }

  // Cloase Confirm Modal
  closeModal() {
    this.setState({ openDeleteModal: false })
  }

  render() {
    const { loading } = this.state
    const vatList = this.props.vat_list
   

    return (
      <div className="vat-code-screen">
        <div className="animated fadeIn">
          <ToastContainer position="top-right" autoClose={3000}  />
          <Card>
            <CardHeader>
              <div className="h4 mb-0 d-flex align-items-center">
                <i className="nav-icon icon-briefcase" />
                <span className="ml-2">Vat Code</span>
              </div>
            </CardHeader>
            <CardBody>
              
            {
              loading ?
                <Loader></Loader>: 
                <Row>
                  <Col lg={12}>
                    <div className="d-flex justify-content-end">
                      <ButtonGroup className="toolbar" size="sm">
                        <Button
                          color="success"
                          className="btn-square"
                        >
                          <i className="fa glyphicon glyphicon-export fa-download mr-1" />
                          Export to CSV
                        </Button>
                        <Button
                          color="info"
                          className="btn-square"
                        >
                          <i className="fa glyphicon glyphicon-export fa-upload mr-1" />
                          Import from CSV
                        </Button>
                        <Button
                          color="primary"
                          className="btn-square"
                          onClick={() => this.props.history.push(`/admin/master/vat-code/create`)}
                        >
                          <i className="fas fa-plus mr-1" />
                          New Code
                        </Button>
                        <Button
                          color="warning"
                          className="btn-square"
                        >
                          <i className="fa glyphicon glyphicon-trash fa-trash mr-1" />
                          Bulk Delete
                        </Button>
                      </ButtonGroup>
                    </div>
                    <div className="py-3">
                      <Form inline>
                        <FormGroup className="pr-3 my-1">
                          <h6 className="m-0">View By : </h6>
                        </FormGroup>
                        <FormGroup className="pr-3 my-1">
                          <Input type="text" placeholder="Vat Name" />
                        </FormGroup>
                        <FormGroup className="pr-3 my-1">
                          <Input type="text" placeholder="Vat Percentage" />
                        </FormGroup>
                      </Form>
                    </div>
                    <BootstrapTable 
                      data={vatList}
                      hover
                      version="4"
                      pagination
                      search={false}
                      selectRow={ this.selectRowProp }
                      options={ this.options }
                      trClassName="cursor-pointer"
                    >
                      <TableHeaderColumn
                        isKey
                        dataField="name"
                        dataSort
                      >
                        Vat Name
                      </TableHeaderColumn>
                      <TableHeaderColumn
                        dataField="vat"
                        dataFormat={this.vatPercentageFormat}
                        dataSort
                      >
                        Vat Percentage
                      </TableHeaderColumn>
                    </BootstrapTable>
                  </Col>
                </Row>
            }
            </CardBody>
          </Card>
          <Modal isOpen={this.state.openDeleteModal}
              className={'modal-danger ' + this.props.className}>
              <ModalHeader toggle={this.toggleDanger}>Delete</ModalHeader>
              <ModalBody>
                  Are you sure want to delete this record?
            </ModalBody>
              <ModalFooter>
                  <Button color="danger" onClick={this.deleteVat}>Yes</Button>&nbsp;
                  <Button color="secondary" onClick={this.closeModal}>No</Button>
              </ModalFooter>
          </Modal>
        </div>
      </div>
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(VatCode)