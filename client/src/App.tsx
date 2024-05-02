import { RouterProvider } from 'react-router-dom';
import router from '@/router/AppRouter';
import { RecoilRoot } from 'recoil';

function App() {
  return (
    <RecoilRoot>
      <RouterProvider router={router} />
    </RecoilRoot>
  );
}

export default App;
