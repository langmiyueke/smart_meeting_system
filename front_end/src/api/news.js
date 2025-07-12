import request from "../utils/request/"
export const newsListService = (params) => {
    return request.get('/news', { params: params })
}
export const newsDeleteService = (id) => {
    return request.delete('/news', { params: { id } })
}
export const tenantListService = () => {
    return request.get('/news/tenant')
}
export const newsEditService = (newsData) => {
    return request.put('/news', newsData)
}
export const newsAddService = (newsData) => {
    return request.post('/news', newsData)
}
export const batchDeleteService = (ids) =>
    request.post('/news/batchDelete', ids)

export const exportSelectedService = (ids) => {
    return request.get('/news/exportSelected', {
        params: { ids },
        responseType: 'blob'
    })
}
export const exportSearchService = (params) =>
    request.get('/news/export', {
        params,
        responseType: 'blob'
    })
