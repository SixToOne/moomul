import { RouteObject, createBrowserRouter } from 'react-router-dom';

import App from '@/App';

// pages
import Home from '@/pages/Home';
import Signup from '@/pages/Signup';
import Login from '@/pages/Login';
import PrivateRoutes from './PrivateRoutes';

const routes: RouteObject[] = [
  {
    element: <App />,
    children: [
      {
        element: <PrivateRoutes />,
        children: [{ path: '/', element: <Login /> }],
      },
      {
        path: '/:username',
        element: <Home />,
      },
      {
        path: '/login',
        element: <Login />,
      },
      {
        path: '/signup',
        element: <Signup />,
      },
    ],
  },
];

const router = createBrowserRouter(routes, { basename: '/' });

export default router;
