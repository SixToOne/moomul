export interface IUser {
  userId: string;
  username: string;
}

export interface ILoginForm {
  username: string;
  password: string;
}

export interface ISignupForm extends ILoginForm {
  nickname: string;
}

export interface IUserProfile {
  nickname: string;
  content: string;
  image: string;
  isMine: boolean;
  toMe: number;
  fromMe: number;
  today: number;
}
