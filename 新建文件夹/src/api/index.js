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


export const sorted = (sortedBy) => {
  return request.get('/course/sorted', {
    params: {
      method: sortedBy
    }
  });
};

export const handleAdd = (data) =>{
  return request.post('/course/handleAdd',data)
}

export const handleRefactor = (data) =>{
  return request.put('/course/handleRefactor',data)
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
