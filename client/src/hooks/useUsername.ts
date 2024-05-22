import { useLocation } from 'react-router-dom';

export const useUsername = () => {
  const { pathname } = useLocation();
  const username = pathname.split('/')[1];

  return username;
};
