import axios from 'axios';

const path = 'users/auth';

interface ReturnType {
  userId: string | undefined;
  username: string | undefined;
  isLogin: boolean;
}

export const checkAuth = async () => {
  const accessToken = localStorage.getItem('accessToken');

  if (!accessToken) {
    return {
      userId: undefined,
      username: undefined,
      isLogin: false,
    };
  }

  axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

  const res = await axios.post<ReturnType>(`${path}`);
  if (res.data) {
    const { isLogin } = res.data;
    if (!isLogin) {
      localStorage.removeItem('accessTokeㅈn');
    }
  }
  return res.data;
};
