import React, { Suspense } from 'react'
import { Route, Switch, Redirect } from 'react-router-dom'
import {connect} from 'react-redux'
import { bindActionCreators } from 'redux'

import { Container } from 'reactstrap'
import {
  AppAside,
  AppBreadcrumb,
  AppFooter,
  AppHeader,
  AppSidebar,
  AppSidebarFooter,
  AppSidebarForm,
  AppSidebarHeader,
  AppSidebarMinimizer,
  AppSidebarNav,
} from '@coreui/react'

import { adminRoutes } from 'routes'
import {
  UserActions,
  CommonActions
} from 'services/global'

import navigation from 'constants/navigation'

import {
  Aside,
  Header,
  Footer,
  Loading
} from 'components'



const mapStateToProps = (state) => {
  return ({
  })
}
const mapDispatchToProps = (dispatch) => {
  return ({
    userActions: bindActionCreators(UserActions, dispatch),
    commonActions: bindActionCreators(CommonActions, dispatch)
  })
}

class AdminLayout extends React.Component {

  constructor(props) {
    super(props)
    this.state = {
    }
  }

  componentDidMount () {
    this.props.userActions.checkAuthStatus()
    if (!window.localStorage.getItem('accessToken')) {
      this.props.history.push('/login')
    }
  }

  render() {

    return (
      <div className="admin-container">
        <div className="app">
          <AppHeader fixed>
            <Suspense fallback={Loading()}>
              <Header {...this.props} />
            </Suspense>
          </AppHeader>
          <div className="app-body">
            <AppSidebar fixed display="lg">
              <AppSidebarHeader />
              <AppSidebarForm />
              <Suspense>
                <AppSidebarNav navConfig={navigation} {...this.props} />
              </Suspense>
              <AppSidebarFooter />
              <AppSidebarMinimizer />
            </AppSidebar>
            <main className="main">
              <AppBreadcrumb appRoutes={adminRoutes} />
              <Container fluid>
                <Suspense fallback={Loading()}>
                  <Switch>
                    {
                      adminRoutes.map((prop, key) => {
                        if (prop.redirect)
                          return <Redirect from={prop.path} to={prop.pathTo} key={key} />
                        return (
                          <Route
                            path={prop.path}
                            component={prop.component}
                            key={key}
                          />
                        )
                      })
                    }
                  </Switch>
                </Suspense>
              </Container>
            </main>
            <AppAside fixed>
              <Suspense fallback={Loading()}>
                <Aside />
              </Suspense>
            </AppAside>
          </div>
          <AppFooter>
            <Suspense fallback={Loading()}>
              <Footer />
            </Suspense>
          </AppFooter>
        </div>
      </div>
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminLayout)