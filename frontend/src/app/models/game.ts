export interface Game {
  id: number;
  title: string;
  description: string;
  releaseDate: string;
  coverUrl: string;
  genre: string;
  platforms: Platform[];
  averageRating: number | null;
  ratingCount: number;
}

export interface Platform {
  id: number;
  name: string;
}
