import React from 'react'
import {connect} from 'react-redux'
import { bindActionCreators } from 'redux'

import './style.scss'

const mapStateToProps = (state) => {
  return ({
  })
}
const mapDispatchToProps = (dispatch) => {
  return ({
  })
}

class VatTransactions extends React.Component {
  
  constructor(props) {
    super(props)
    this.state = {
      
    }

  }

  render() {

    return (
      <div className="vat-transactions-screen">
        <div className="animated fadeIn">
        </div>
      </div>
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(VatTransactions)