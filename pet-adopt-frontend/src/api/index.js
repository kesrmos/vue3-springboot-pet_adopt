import * as userApi from './user'
import * as petApi from './pet'
import * as applyApi from './apply'

/**
 * 接口统一出口
 * 用法：import { petApi, applyApi } from '@/api'
 * 各页面也可以继续按需直接引入具体模块，两者等价
 */
export { userApi, petApi, applyApi }
