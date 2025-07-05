import request from "../utils/request/"

export const getLogin = (info) => {
  return request.post("/user/login",info);
};


export const getCourses = (data) => {
  return request.post("/course/getCourses",data);
};


export const deleteCourse = (id) => {
  return request.get("/course/deleteCourse", {
    params: {
      id: id
    }
  });
};

//从后端读取会议信息的方法
export const getMeetings = (data) => {
  return request.post("/meeting/getMeetings",data);
};

//删除会议信息的方法
export const deleteMeeting = (id) => {
  return request.get("/meeting/deleteMeeting", {
    params: {
      id: id
    }
  });
};

//审核会议通过的方法
export const reViewMeeting = (id) => {
  return request.get("/meeting/reViewMeeting", {
    params: {
      id: id
    }
  });
};

// 添加获取单个会议详情的接口
export const getMeetingById = (id) => {
return request.get("/meeting/getMeetingById", {
    params: {
      id: id
    }
  });
}


export const handleAdd = (target, data) => {
  return request.post(`/${target}/handleAdd`, data)
}

export const handleRefactor = (target, data) =>{
  return request.put(`/${target}/handleRefactor`, data)
}

export const getUserInfo = () => {
  return request.get("/user/getUserInfo");
};

export const ToRegister= (RegisterData) =>{
  return request.post("/user/register",RegisterData)
  
}

export const clearIsDeleted = () =>{
    return request.get("/course/clearIsDeleted")
}

export const verifyCode = (phone,code) =>{
   
   const data = {}
   data.phoneNumber = phone;
   data.code=code;
   return request.post("/user/verifyCode",
    data
   )
}

export const removeFile = (fileName)=>{
  return request.get("/upload/removeFile",
   {
    params: {
      fileName: fileName
    }
  }
  )
} 


export const sendVerificationCode = (phoneNumber)=>{
  return request.get("/user/verificationCode",
    {
      params:{
        phoneNumber:phoneNumber
      }
    }
  )
}
