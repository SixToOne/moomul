import { RouteObject, createBrowserRouter } from 'react-router-dom';

// pages
import Home from '@/pages/Home';
import Login from '@/pages/Login';

const routes: RouteObject[] = [
  {
    path: '/',
    element: <Home />,
  },
  {
    path: '/login',
    element: <Login />,
  },
];

const router = createBrowserRouter(routes);

export default router;
