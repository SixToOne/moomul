import { useCallback, useEffect, useRef } from 'react';

interface IUseProgress {
  initialValue: number;
  value: number;
  incrementMod?: number;
}

const useProgress = ({ initialValue, value, incrementMod = 2 }: IUseProgress) => {
  const ref = useRef<HTMLProgressElement>(null);
  let rAFId = 0;

  const callback = useCallback(() => {
    if (!ref.current || ref.current.value >= value) return;
    ref.current.value =
      ref.current.value + incrementMod <= value ? ref.current.value + incrementMod : value;
    rAFId = requestAnimationFrame(callback);
  }, [ref, value]);

  useEffect(() => {
    if (!ref.current) return;
    ref.current.value = initialValue;
    rAFId = requestAnimationFrame(callback);
    return () => cancelAnimationFrame(rAFId);
  }, [initialValue, value, callback]);

  return ref;
};

export default useProgress;
