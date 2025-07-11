import { defineStore } from 'pinia';
import { getToken, removeToken, setToken } from '../utils/token-utils';
import { getLogin,getUserInfo } from '../api/index';


/**
 * 用户信息
 * @methods setUserInfos 设置用户信息
 */
export const useUserInfoStore = defineStore('userInfo', {

	state: () => ({
    token: getToken(),
    name: '',
    role: '',
    id:''
  }),

	actions: {
    // 登陆的异步action
 
    async login (loginForm) {
       // 发送登陆的请求
      const result = await getLogin(loginForm)
      // 请求成功后, 取出token保存  pinia和local中
      const token = result.token
      this.token = token
      setToken(token)
    },
     async getInfo () {
      const result = await getUserInfo()
      this.role = result.role
      this.name = result.name
      this.id = result.loginUser.id
    }, 
    initUserInfo(){
      removeToken()
      this.role = ""
      this.id = ""
      this.name = ""
      console.log('1111111111');
    }
  },
  

});