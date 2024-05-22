import { atom } from 'recoil';
import { IUser } from '@/apis/user/user';

const ATOM_KEY = 'userSession';

const userSessionAtom = atom<IUser | undefined>({
  key: ATOM_KEY,
  default: undefined,
});

export default userSessionAtom;
