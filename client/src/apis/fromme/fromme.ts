export interface IFromMe {
  id: number;
  nickname: string;
  content: string;
  options: IVoteOption[];
  voted: number | null;
  voteCnt: number;
  reply: string | null;
  likeCnt: number;
  commentCnt: number;
  liked: boolean;
  createdAt: string;
}

export interface IVoteOption {
  id: number;
  content: string;
  voteCnt: number;
}
