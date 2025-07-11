import request from "../utils/request/"
// 封装的请求方法
export const getDeptTree = () => {
  return request.get('/dept/tree');
};

export const getDeptPage = (data) => {
  return request.post('/dept/page', data);
};

export const addDept = (data) => {
  return request.post('/dept', data);
};

export const updateDept = (data) => {
  return request.put('/dept', data);
};

export const deleteDept = (id) => {
  return request.delete(`/dept/${id}`);
};
