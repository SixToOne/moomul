export interface IUser {
  userId: string;
}

export interface ILoginForm {
  username: string;
  password: string;
}

export interface ISignupForm extends ILoginForm {
  nickname: string;
}
