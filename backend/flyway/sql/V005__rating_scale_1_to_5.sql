ALTER TABLE public.ratings
    DROP CONSTRAINT ratings_rating_check;

UPDATE public.ratings
SET rating = rating + 1
WHERE rating BETWEEN 0 AND 4;

ALTER TABLE public.ratings
    ADD CONSTRAINT ratings_rating_check CHECK (rating BETWEEN 1 AND 5);
