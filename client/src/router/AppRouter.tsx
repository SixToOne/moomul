import { RouteObject, createBrowserRouter } from 'react-router-dom';

import App from '@/App';

// pages
import Home from '@/pages/Home';
import Signup from '@/pages/Signup';
import Login from '@/pages/Login';
import Write from '@/pages/Write';
import Landing from '@/pages/Landing';
import PrivateRoutes from './PrivateRoutes';

const routes: RouteObject[] = [
  {
    element: <App />,
    children: [
      {
        element: <PrivateRoutes />,
        children: [{ path: '/', element: <Landing /> }],
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
      {
        path: '/:username/write',
        element: <Write />,
      },
    ],
  },
];

const router = createBrowserRouter(routes, { basename: '/' });

export default router;
