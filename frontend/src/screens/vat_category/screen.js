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

class VatCategory extends React.Component {
  
  constructor(props) {
    super(props)
    this.state = {
      
    }

  }

  render() {

    return (
      <div className="vat-category-screen">
        <h1>VatCategory Screen</h1>
      </div>
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(VatCategory)