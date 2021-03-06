import System.IO as IO
import System.Environment
import Data.List as L
import NumberTuples

main = do
	(pointString:lenString:nextKeyString:_) <- getArgs
	let points = parsePoints pointString;
		nextKey = (read nextKeyString :: Int);
		gravityPoints = doGravity points (read lenString :: Int);
		processedPoints = removeCollisions $ gravityPoints;
		finalAccumulation = getListOfNewPoints gravityPoints nextKey ++ processedPoints;
		futureNextKey = show $ 1 + (getHighestKey $ finalAccumulation);
	IO.putStrLn $ (show $ finalAccumulation) ++ " aaa " ++ futureNextKey

type Key = Int
type Force = Double
type Accelleration = Double
type Velocity = Double
type Position = Double

data ComplexPoint = ComplexPoint {
	key :: Int,
	x :: Position,
	y :: Position,
	r :: Double,
	xVel :: Velocity,
	yVel :: Velocity,
	xAcc :: Accelleration,
	yAcc :: Accelleration,
	mass :: Double
} deriving(Show, Read, Eq)



-- Constants
damp :: Double
damp = 10.5

gC :: Double
gC = 0.0000000000667408

timeConstant :: Double
timeConstant = 200

density :: Double
density = 2400;



-- Collisions
processCollisions' :: String -> Key -> String
processCollisions' points key = let pointsFromListSource = getListFromTuples processedTuples;
							   aPoints = parsePoints points;
							   processedTuples = processTuples . filterDupes . getCollisions $ aPoints
							   in (show (getPointsFromList pointsFromListSource aPoints key)) ++ " aaa " ++ (show $ L.concat processedTuples)

processCollisions :: [ComplexPoint] -> [Key]
processCollisions points = L.concat $ processTuples . filterDupes . getCollisions $ points

getListOfNewPoints :: [ComplexPoint] -> Key -> [ComplexPoint]
getListOfNewPoints points nextKey = let pointsFromListSource = getListFromTuples processedTuples;
									processedTuples = processTuples . filterDupes . getCollisions $ points
									in getPointsFromList pointsFromListSource points nextKey

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
												sqrt(((x2-x1)^2) + ((y2-y1)^2)) <= (r1 + r2)]

parsePoints :: String -> [ComplexPoint]
parsePoints string = read string :: [ComplexPoint]

filterDupes :: [(Key, Key)] -> [(Key, Key)]
filterDupes list = L.nub $ [(lower, upper) | (key1,key2) <- list, let lower = min key1 key2, let upper = max key1 key2]

getListFromTuples :: [[Key]] -> [([Key], Key)]
getListFromTuples listOfTuples = zip listOfTuples [0..]

getPointsFromList :: [([Key], Key)] -> [ComplexPoint] -> Key -> [ComplexPoint]
getPointsFromList keys points newKey = [point | (keyList,addToKey) <- keys, let point = mergePoints keyList points (newKey + addToKey)]

mergePoints :: [Key] -> [ComplexPoint] -> Key -> ComplexPoint
mergePoints keys points newKey = combine ([newPoint | newPoint <- points, (key newPoint) `elem` keys]) newKey

combine :: [ComplexPoint] -> Key -> ComplexPoint
combine points newKey = let (xVel, yVel, newMass) = momentum $ [(xV, yV, m) | (ComplexPoint _ _ _ _ xV yV _ _ m) <- points];
				 	 xCenter = (sum $ [xPos * m | point <- points, let xPos = (x point), let m = mass point]) / totalMass;
				 	 yCenter = (sum $ [yPos * m | point <- points, let yPos = (y point), let m = mass point]) / totalMass;
				 	 newR = 3 `nthRoot` (totalMass * 3.0) / (4.0 * pi * density);
				 	 totalMass = sum $ [m | point <- points, let m = mass point];
				 	 xAcc = 0;
				 	 yAcc = 0
		 	 	 in ComplexPoint newKey xCenter yCenter newR xVel yVel xAcc yAcc newMass

momentum :: [(Velocity, Velocity, Double)] -> (Velocity, Velocity, Double)
momentum dataPoints = let xVel = (sum $ [vel*mass | (vel,_,mass) <- dataPoints]) / mass;
					  yVel = (sum $ [vel*mass | (_,vel,mass) <- dataPoints]) / mass;
					  mass = sum $ [mass | (_,_,mass) <- dataPoints]
					  in (xVel, yVel, mass)



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
force (ComplexPoint _ x1 y1 _ _ _ _ _ mass1) (ComplexPoint _ x2 y2 _ _ _ _ _ mass2) = let distance = sqrt((x2-x1)^2 + (y2-y1)^2)
																				  in (gC * mass1 * mass2 * (abs(distance))) / (abs(distance)^2 + damp^2)**(3/2)

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
					 			 in addToVelocity point $ mapTriple (* timeConstant) acc

findPosition :: ComplexPoint -> [ComplexPoint] -> (Position, Position, Key)
findPosition point otherPoints = let xPos = (x point) + (firstElem velocity)*timeConstant; yPos = (y point) + (secondElem velocity) * timeConstant; velocity = findVelocity point otherPoints
							     in (xPos, yPos, (key point))

changePoint :: ComplexPoint -> [ComplexPoint] -> ComplexPoint
changePoint point otherPoints = let position = findPosition point otherPoints; velocity = findVelocity point otherPoints; accelleration = findAccelleration point otherPoints
								in ComplexPoint (thirdElem position) (firstElem position) (secondElem position) (r point) (firstElem velocity) (secondElem velocity) (firstElem accelleration) (secondElem accelleration) (mass point)


-- Gravity engine core
doGravity :: [ComplexPoint] -> Int -> [ComplexPoint]
doGravity (point:[]) _ = [point]
doGravity _ 0 = []
doGravity (point1:point2:rest) numberOfThings = (changePoint point1 (point2:rest)) : doGravity ((point2:rest) ++ [point1]) (numberOfThings-1)



-- Helper functions
removeCollisions :: [ComplexPoint] -> [ComplexPoint]
removeCollisions p = [validPoint | validPoint <- p, let returnList = processCollisions p, not $ (key validPoint) `elem` returnList]

getHighestKey :: [ComplexPoint] -> Key
getHighestKey point = head . reverse . sort $ [k | p <- point, let k = key p]

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

addToVelocity :: ComplexPoint -> (Velocity, Velocity, Key) -> (Velocity, Velocity, Key)
addToVelocity point (xV, yV, k) = (xV + (xVel point), yV + (yVel point), k)

nthRoot :: (Floating b, Eq b) => b -> b -> b
n `nthRoot` x = fst $ until (uncurry(==)) (\(_,x0) -> (x0,((n-1)*x0+x/x0**(n-1))/n)) (x,x/n)