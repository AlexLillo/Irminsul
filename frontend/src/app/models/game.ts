export interface Game {
  id: number;
  title: string;
  description: string | null;
  releaseDate: string | null;
  coverUrl: string | null;
  genre: string;
  platforms: Platform[];
  averageRating: number | null;
  ratingCount: number;
}

export interface Platform {
  id: number;
  name: string;
}
