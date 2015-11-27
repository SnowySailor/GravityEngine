import System.IO as IO
import System.Environment
import Data.List as L

main = do
	(points:_) <- getArgs
	IO.putStrLn $ processPoints points

--Point :: Double -> Double -> Double -> Point x y r
data Point = Point {
	key :: Int,
	x :: Double,
	y :: Double,
	r :: Double
} deriving (Show, Read, Eq)

type Key = Int

processPoints :: String -> String
processPoints points = show . filterDupes . getCollisions . parsePoints $ points

getCollisions :: [Point] -> [(Key, Key)]
getCollisions list = goThrough list $ length list

goThrough :: [Point] -> Int -> [(Key, Key)]
goThrough _ 0 = []
goThrough (point1:point2:rest) times = goThrough ((point2:rest) ++ [point1]) (times-1) ++ matches point1 (point2:rest)

matches :: Point -> [Point] -> [(Key, Key)]
matches (Point key1 x1 y1 r1) pointList = [points | (Point key2 x2 y2 r2) <- pointList, let points = (key1,key2), sqrt((x2-x1)^2 + (y2-y1)^2) <= (r1 + r2)]

parsePoints :: String -> [Point]
parsePoints string = read string :: [Point]

filterDupes :: [(Key, Key)] -> [(Key, Key)]
filterDupes list = L.nub $ [(lower, upper) | (key1,key2) <- list, let lower = min key1 key2, let upper = max key1 key2]