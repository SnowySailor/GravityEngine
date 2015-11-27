import System.IO as IO
import System.Environment
import Data.List as L

main = do
	(points:len:_) <- getArgs
	IO.putStrLn $ (show $ doGravity (read points :: [ComplexPoint]) (read len :: Int)) ++ " aaa " ++ processCollisions points

--data SimplePoint = SimplePoint {
--	sKey :: Int,
--	sX :: Double,
--	sY :: Double,
--	sR :: Double
--} deriving (Show, Read, Eq)

data ComplexPoint = ComplexPoint {
	key :: Int,
	x :: Double,
	y :: Double,
	r :: Double,
	xVel :: Double,
	yVel :: Double,
	xAcc :: Double,
	yAcc :: Double,
	mass :: Double
} deriving(Show, Read, Eq)

type Key = Int
type Force = Double
type Accelleration = Double
type Velocity = Double
type Position = Double

gC :: Double
gC = 0.0000000000667408

timeConstant :: Double
timeConstant = 50

-- Collisions
processCollisions :: String -> String
processCollisions points = show . filterDupes . getCollisions . parsePoints $ points

getCollisions :: [ComplexPoint] -> [(Key, Key)]
getCollisions (_:[]) = []
getCollisions list = goThrough list $ length list

goThrough :: [ComplexPoint] -> Int -> [(Key, Key)]
goThrough _ 0 = []
goThrough (point1:point2:rest) times = goThrough ((point2:rest) ++ [point1]) (times-1) ++ matches point1 (point2:rest)

matches :: ComplexPoint -> [ComplexPoint] -> [(Key, Key)]
matches point1 pointList = [points | point2 <- pointList, 
												let x1 = x point1,
												let x2 = x point2,
												let y1 = y point1,
												let y2 = y point2,
												let r1 = r point1,
												let r2 = r point2,
												let points = (key point1,key point2), 
												sqrt((x2-x1)^2 + (y2-y1)^2) <= (r1 + r2)]

parsePoints :: String -> [ComplexPoint]
parsePoints string = read string :: [ComplexPoint]

filterDupes :: [(Key, Key)] -> [(Key, Key)]
filterDupes list = L.nub $ [(lower, upper) | (key1,key2) <- list, let lower = min key1 key2, let upper = max key1 key2]

-- Movement
getXYForces :: ComplexPoint -> [ComplexPoint] -> [(Force, Force)]
getXYForces point1 otherPoints = [(xForce, yForce) | point2 <- otherPoints, 
								let x1 = x point1,
								let x2 = x point2,
								let y1 = y point1,
								let y2 = y point2,
								let xSign = sign x1 x2,
								let ySign = sign y1 y2,
								let xForce = xSign * abs(cos(atan(((y point2) - (y point1))/((x point2) - (x point1)))) * (force point1 point2)), 
								let yForce = ySign * abs(sin(atan(((y point2) - (y point1))/((x point2) - (x point1)))) * (force point1 point2))]

sign :: Double -> Double -> Double
sign point1 point2 
	| (point2 - point1) >= 0 = 1 
	| (point2 - point1) < 0  = -1
	| otherwise              = 1

force :: ComplexPoint -> ComplexPoint -> Force
force (ComplexPoint _ x1 y1 _ _ _ _ _ mass1) (ComplexPoint _ x2 y2 _ _ _ _ _ mass2) = (gC * mass1 * mass2) / ((sqrt((x2-x1)^2 + (y2-y1)^2)))

sumForces :: [(Force, Force)] -> (Force, Force)
sumForces forces = let xForce = sum [x | (x,_) <- forces]; yForce = sum [y | (_,y) <- forces]
                   in (xForce, yForce)

findAccelleration :: ComplexPoint -> [ComplexPoint] -> (Accelleration, Accelleration, Key)
findAccelleration point1 [] = (0,0,(key point1))
findAccelleration point1 otherPoints =  let (xAcc, yAcc) = mapTuple (/(mass point1)) $ (sumForces $ getXYForces point1 otherPoints); pKey = (key point1)
										in (xAcc, yAcc, pKey)

findVelocity :: ComplexPoint -> [ComplexPoint] -> (Velocity, Velocity, Key)
findVelocity point [] = (xVel point, yVel point, key point)
findVelocity point otherPoints = let acc = findAccelleration point otherPoints
					 			 in mapTriple (* timeConstant) acc

findPosition :: ComplexPoint -> [ComplexPoint] -> (Position, Position, Key)
findPosition point otherPoints = let xPos = (x point) + (firstElem velocity)*timeConstant; yPos = (y point) + (secondElem velocity) * timeConstant; velocity = findVelocity point otherPoints
							     in (xPos, yPos, (key point))

changePoint :: ComplexPoint -> [ComplexPoint] -> ComplexPoint
changePoint point otherPoints = let position = findPosition point otherPoints; velocity = findVelocity point otherPoints; accelleration = findAccelleration point otherPoints
								in ComplexPoint (thirdElem position) (firstElem position) (secondElem position) (r point) (firstElem velocity) (secondElem velocity) (firstElem accelleration) (secondElem accelleration) (mass point)

doGravity :: [ComplexPoint] -> Int -> [ComplexPoint]
doGravity (point:[]) _ = [point]
doGravity _ 0 = []
doGravity (point1:point2:rest) numberOfThings = (changePoint point1 (point2:rest)) : doGravity ((point2:rest) ++ [point1]) (numberOfThings-1) 

showThings :: [ComplexPoint] -> String
showThings points = show points

mapTuple :: (a -> b) -> (a, a) -> (b, b) 
mapTuple f (t1, t2) = (f t1, f t2)

mapTriple :: (b -> b) -> (b, b, a) -> (b, b, a)
mapTriple f (t1,t2,t3) = (f t1, f t2, t3)

firstElem :: (a, a, b) -> a
firstElem (x,_,_) = x

secondElem :: (a, a, b) -> a
secondElem (_,y,_) = y

thirdElem :: (a,a,b) -> b
thirdElem (_,_,k) = k