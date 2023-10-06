package main

import (
	"container/list"
	"math/rand"
	"sync/atomic"
)

type Graph struct {
	readersCount atomic.Int32
	writersCount atomic.Int32
	nodesCount   int
	graph        map[int]list.List
}

func NewGraph() (*Graph, error) {
	graph := &Graph{}
	graph.graph = make(map[int]list.List)
	return graph, nil
}

func (graph *Graph) updatePrice() {
	for graph.readersCount.Load() > 0 && graph.writersCount.Load() > 0 {
	}

	graph.writersCount.Add(1)
	for _, edges := range graph.graph {
		front := edges.Front()
		for i := 0; i < edges.Len(); i++ {
			var shouldUpdate bool
			shouldUpdate = rand.Intn(2)%2 == 1

			if shouldUpdate {
				front.Value.([]int)[1] = rand.Int()
			}
		}
	}
	graph.writersCount.Add(-1)
}

func (graph *Graph) addOrRemoveEdges() {
	for graph.readersCount.Load() > 0 && graph.writersCount.Load() > 0 {
	}

	graph.writersCount.Add(1)
	for _, edges := range graph.graph {
		if rand.Intn(2)%2 == 1 {
			index := rand.Intn(edges.Len())
			position := edges.Front()
			for i := 0; i <= index; i++ {
				position = position.Next()
			}
			edges.Remove(position)
		}
		if rand.Intn(2)%2 == 1 {
			index := rand.Intn(graph.nodesCount)
			var edge [2]int
			edge[0] = index
			edge[1] = rand.Int()
			edges.PushBack(edge)
		}
	}
	graph.writersCount.Add(-1)
}

func (graph *Graph) addOrRemoveNode() {
	for graph.readersCount.Load() > 0 && graph.writersCount.Load() > 0 {
	}

	graph.writersCount.Add(1)
	if rand.Intn(2)%2 == 1 {
		graph.nodesCount = graph.nodesCount + 1
	}
	graph.writersCount.Add(-1)
}

func (graph *Graph) isThereAPath(from int, to int) bool {
	for graph.readersCount.Load() > 0 {
	}

	graph.readersCount.Add(1)
	var visited = make([]bool, graph.nodesCount)
	dfs(from, &visited, graph)
	graph.readersCount.Add(-1)
	return visited[to]
}

func dfs(v int, visited *[]bool, graph *Graph) {
	(*visited)[v] = true

	l := graph.graph[v]
	front := l.Front()

	for front != nil {
		to := front.Value.([2]int)[0]
		if !(*visited)[to] {
			dfs(to, visited, graph)
		}
	}
}

func main() {
	graph, _ := NewGraph()
	go graph.updatePrice()
	go graph.isThereAPath(0, 10)
	go graph.addOrRemoveEdges()
	go graph.addOrRemoveNode()
}
