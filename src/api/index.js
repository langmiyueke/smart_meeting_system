import request from "../utils/request/"

export const getLogin = (info) => {
  return request.post("user/login",info);
};

export const getUserInfo = () => {
  return request.get("user/getUserInfo");
};