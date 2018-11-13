import Taro, {Component} from '@tarojs/taro'
import {View} from '@tarojs/components'
import ScrollImg from '../components/swiper/index'
import List from '../components/list/index'
import Navigation from '../components/Navigation/index'
import {PAGEINFO} from '../../utils/api'
import {fetch} from '../../utils/fetch'
import {getG, setG} from '../../utils/globalData'

export default class Index extends Component {

  config = {
    navigationBarTitleText: '${pageData.pageTitle}',
  }

  constructor() {
    super(...arguments)
    this.state = {
      pageData: {
        comResult: []
      },
    }
  }

  componentWillMount() {
    let pageData = getG('pageData');
    if (pageData) {
      this.setState({pageData: getG('pageData')[${pageIndex}]})
    }
  }

  componentDidMount() {

  }

  componentWillUnmount() {
  }

  componentDidShow() {
    if (!getG('pageData')) {
      fetch(PAGEINFO, {appId: getG('appId')}).then(res => {
        Taro.setStorage({
          key: "pageData",
          data: res.data
        })
        setG('pageData', JSON.parse(res.data.content))
        this.setState({pageData: getG('pageData')[${pageIndex}]})
      })
    }
  }

  componentDidHide() {

  }

  render() {
    let {pageData} = this.state
    let comData = pageData.comResult
    const CusComponent = comData.map((obj, index) => {
      return obj.name === 'ScrollImg' ?
      <ScrollImg key={index} imgs={obj.data.imgs}></ScrollImg> :
        obj.name === 'List' ? <List></List> :
        obj.name === 'Navigation' ? <Navigation data={obj.data}></Navigation> :
          <View>无组件</View>
    })
    return (
      <View className='main'>
        {CusComponent}
      </View>
    )
  }
}

