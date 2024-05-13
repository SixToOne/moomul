import { useEffect } from 'react';
import { useSetRecoilState } from 'recoil';
import userSessionAtom from '@/recoil/atoms/userSession';
import { checkAuth } from '@/apis/user/check-auth';

const useAuth = () => {
  const setUserSession = useSetRecoilState(userSessionAtom);

  const getToken = async () => {
    const data = await checkAuth();
    if (data && data.isLogin) {
      setUserSession({
        userId: data.userId as string,
        username: data.username as string,
      });
    }
  };

  useEffect(() => {
    getToken();
  }, [setUserSession]);
};

export default useAuth;
