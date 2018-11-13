import Taro, {Component} from '@tarojs/taro'
import Index from './pages/index'

import './app.scss'

class App extends Component {

    config = {
        pages: [
        <#list PageList as field>
        'pages/generator/index_${field_index}',
        </#list>
        'pages/login/login',
      'pages/List/index',
      'pages/center/index',
      'pages/about/index',
      'pages/detail/index',
    ],
    window: {
        backgroundTextStyle: 'light',
        navigationBarBackgroundColor: '#fff',
        navigationBarTitleText: 'WeChat',
        navigationBarTextStyle: 'black'
    },
<#if PageList?size&gt;1>
    tabBar: {
        list: [

            <#list PageList as field>
            {
                pagePath: 'pages/generator/index_${field_index}',
                text: "${field.pageName}",
                iconPath: 'image/home.png',
                selectedIconPath: 'image/homeactive.png'
            },
             </#list>

        ]
    },
</#if>
}

componentDidMount() {
}

componentDidShow() {
}

componentDidHide() {
}

componentCatchError() {
}

render() {
    return (
        <Index/>
)
}
}

Taro.render(<App/>, document.getElementById('app'))
