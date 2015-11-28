module NumberTuples where

import Control.Monad
import Data.List

processTuples s = nub $ themsRelateds $ concatRelated $ findRelated s 

findRelated s = do
    (a,b) <- s
    let related = do
        (a',b') <- s
        guard $ or [a == a', a == b', b == a', b == b']
        guard $ (a,b) /= (a',b')
        return (a',b')
    return (a,b,related)

traverseRelated s a b a' b' up = do
    (a'',b'',related'') <- s
    guard $ (a ,b ) /= (a'',b'')
    guard $ (a',b') == (a'',b'')
    (c,d) <- related''
    let up' = if elem (a,b) up
        then up
        else (a,b) : sort up
    if elem (c,d) up
        then return $ up'
        else traverseRelated s a' b' c d up'

concatRelated s = do
    (a,b,related) <- s
    let v = do
        (a',b') <- related
        rs <- traverseRelated s a b a' b' []
        (ra,rb) <- rs
        return (ra,rb)
    let v2 = map head . group . sort $ (a,b):v
    return (a,b,v2)

themsRelateds s = map (map head . group . sort) $ map ex gs
    where
        gs = map head . group . map (\(_,_,r) -> r) $ s
        ex [] = []
        ex ((a,b):rs) = a : b : ex rs