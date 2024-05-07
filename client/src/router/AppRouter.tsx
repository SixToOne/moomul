import { RouteObject, createBrowserRouter } from 'react-router-dom';

import App from '@/App';

// pages
import Home from '@/pages/Home';
import LogIn from '@/pages/Login';
import Signup from '@/pages/Signup';

const routes: RouteObject[] = [
  {
    element: <App />,
    children: [
      {
        path: '/',
        element: <Home />,
      },
      {
        path: '/login',
        element: <LogIn />,
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
