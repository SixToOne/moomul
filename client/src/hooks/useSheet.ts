import { useState } from 'react';

export const useSheet = () => {
  const [open, setOpen] = useState<boolean>(false);

  const openSheet = () => {
    setOpen(true);
  };

  const closeSheet = () => {
    setOpen(false);
  };

  return { open, openSheet, closeSheet };
};
