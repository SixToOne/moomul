import userSessionAtom from '@/recoil/atoms/userSession';
import { Navigate, Outlet } from 'react-router-dom';
import { useRecoilValue } from 'recoil';

const PrivateRoutes = () => {
  const userSession = useRecoilValue(userSessionAtom);
  if (userSession) return <Navigate to={`/${userSession.username}`} />;
  return <Outlet />;
};

export default PrivateRoutes;
