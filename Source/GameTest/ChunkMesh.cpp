// Fill out your copyright notice in the Description page of Project Settings.

#include "ChunkMesh.h"
#include "qef_simd.h"
#include "WorldObjectsHandler.h"
#include "SphereDestructionObject.h"
#include "ConstructorHelpers.h"
//#include "UnrealMemory.h"
//#include "Timer.h"

// Sets default values

//const FVector VoxelVertecies[] = { FVector(0, 0, 0),  FVector(1, 0, 0),
//FVector(1, 0, 1),  FVector(0, 0, 1),  FVector(0, 1, 0),  FVector(1, 1, 0),
//FVector(1, 1, 1),  FVector(0, 1, 1) };

const FVector VoxelVertecies[] = { FVector(0, 0, 0),  FVector(0, 0, 1),
FVector(0, 1, 0),  FVector(0, 1, 1),  FVector(1, 0, 0),  FVector(1, 0, 1),
FVector(1, 1, 0),  FVector(1, 1, 1) };


const int edgevmap[12][2] =
{
	{ 0,4 },{ 1,5 },{ 2,6 },{ 3,7 },	// x-axis 
	{ 0,2 },{ 1,3 },{ 4,6 },{ 5,7 },	// y-axis
	{ 0,1 },{ 2,3 },{ 4,5 },{ 6,7 }		// z-axis
};

const int faceMap[6][4] = { { 4, 8, 5, 9 },{ 6, 10, 7, 11 },{ 0, 8, 1, 10 },{ 2, 9, 3, 11 },{ 0, 4, 2, 6 },{ 1, 5, 3, 7 } };
const int cellProcFaceMask[12][3] = { { 0,4,0 },{ 1,5,0 },{ 2,6,0 },{ 3,7,0 },{ 0,2,1 },{ 4,6,1 },{ 1,3,1 },{ 5,7,1 },{ 0,1,2 },{ 2,3,2 },{ 4,5,2 },{ 6,7,2 } };
const int cellProcEdgeMask[6][5] = { { 0,1,2,3,0 },{ 4,5,6,7,0 },{ 0,4,1,5,1 },{ 2,6,3,7,1 },{ 0,2,4,6,2 },{ 1,3,5,7,2 } };

const int faceProcFaceMask[3][4][3] = {
	{ { 4,0,0 },{ 5,1,0 },{ 6,2,0 },{ 7,3,0 } },
	{ { 2,0,1 },{ 6,4,1 },{ 3,1,1 },{ 7,5,1 } },
	{ { 1,0,2 },{ 3,2,2 },{ 5,4,2 },{ 7,6,2 } }
};

const int faceProcEdgeMask[3][4][6] = {
	{ { 1,4,0,5,1,1 },{ 1,6,2,7,3,1 },{ 0,4,6,0,2,2 },{ 0,5,7,1,3,2 } },
	{ { 0,2,3,0,1,0 },{ 0,6,7,4,5,0 },{ 1,2,0,6,4,2 },{ 1,3,1,7,5,2 } },
	{ { 1,1,0,3,2,0 },{ 1,5,4,7,6,0 },{ 0,1,5,0,4,1 },{ 0,3,7,2,6,1 } }
};

const int edgeProcEdgeMask[3][2][5] = {
	{ { 3,2,1,0,0 },{ 7,6,5,4,0 } },
	{ { 5,1,4,0,1 },{ 7,3,6,2,1 } },
	{ { 6,4,2,0,2 },{ 7,5,3,1,2 } },
};

const int processEdgeMask[3][4] = { { 3,2,1,0 },{ 7,5,6,4 },{ 11,10,9,8 } };


const FVector VoxelEdges[3][4] = { {FVector(0,0,0),FVector(0,0,1),FVector(0,1,1),FVector(0,1,0)},{FVector(0,0,0),FVector(0,0,1),FVector(1,0,1),FVector(1,0,0)},{FVector(0,0,0),FVector(0,1,0),FVector(1,1,0),FVector(1,0,0)} };
const FVector NearestVoxels[3][4] = { { FVector(0,0,0),FVector(0,-1,0),FVector(0,-1,-1),FVector(0,0,-1) },{ FVector(0,0,0),FVector(0,0,-1),FVector(-1,0,-1),FVector(-1,0,0) },{ FVector(0,0,0),FVector(-1,0,0),FVector(-1,-1,0),FVector(0,-1,0) } };

typedef std::pair<float, ITerrainObjectInterface*> VoxelVertexData;

IMPLEMENT_ALLOCATOR(ChunkOctreeNode, 2396745, 0)

IMPLEMENT_ALLOCATOR(OctreeDrawInfo, 2396745, 0)


AChunkMesh::AChunkMesh()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;
	Mesh = CreateDefaultSubobject<UProceduralMeshComponent>(TEXT("Mesh"));
	MaterialList.Add((ConstructorHelpers::FObjectFinder<UMaterial>(TEXT("UMaterial'/Game/Resources/Materials/TestStone'"))).Object);
	MaterialList.Add((ConstructorHelpers::FObjectFinder<UMaterial>(TEXT("UMaterial'/Game/Resources/Materials/TestDirt'"))).Object);
	RootComponent = Mesh;

}


int TriangleIndex = 0;

void ContourProcessEdge(ChunkOctreeNode* node[4], int dir, TArray<int32>* Indecies, AChunkMesh* chunk, int MaterialIndex) {
	float minSize = MAX_FLT;
	int minIndex = 0;
	int32 indecies[4] = { -1,-1,-1,-1 };
	bool flip = false;
	bool signChange[4] = { false,false,false,false };
	bool HasMaterial[4] = { false,false,false,false };
	bool AllVertsHasMaterial = true;
	for (int i = 0; i < 4; i++) {
		const int edge = processEdgeMask[dir][i];
		const int c1 = edgevmap[edge][0];
		const int c2 = edgevmap[edge][1];
		const int m1 = (node[i]->DrawInfo->corners >> c1) & 1;//What happened here? Questions, questions...
		const int m2 = (node[i]->DrawInfo->corners >> c2) & 1;//That's the lesson, chuldren. Never use the forbidden spells... heck, never copy the code you do not understand
		for (int j = 0; j < 8; j++) {
			HasMaterial[i] = HasMaterial[i] || (node[i]->DrawInfo->Materials[j] == MaterialIndex);
		}
		if (!HasMaterial[i]) {
			AllVertsHasMaterial = false;
			break;
		}
		if (node[i]->size < minSize) {
			minSize = node[i]->size;
			minIndex = i;
			flip = m1 != 0;
		}
		indecies[i] = ((node[i]->DrawInfo->index));
		signChange[i] =
			(m1 == 0 && m2 != 0) ||
			(m1 != 0 && m2 == 0);
	}
	if(AllVertsHasMaterial)
		if (signChange[minIndex])
		{
			if (flip)
			{
				Indecies->Add(indecies[0]);
				Indecies->Add(indecies[1]);
				Indecies->Add(indecies[3]);

				Indecies->Add(indecies[0]);
				Indecies->Add(indecies[3]);
				Indecies->Add(indecies[2]);
			}
			else
			{
				Indecies->Add(indecies[0]);
				Indecies->Add(indecies[3]);
				Indecies->Add(indecies[1]);

				Indecies->Add(indecies[0]);
				Indecies->Add(indecies[2]);
				Indecies->Add(indecies[3]);
			}

		}

}

void ContourEdgeProc(ChunkOctreeNode* node[4], int dir, TArray<int32>* Indecies, AChunkMesh* chunk, int MaterialIndex) {
	if (!node[0] || !node[1] || !node[2] || !node[3])
	{
		return;
	}
	if (node[0]->type != Node_Internal &&
		node[1]->type != Node_Internal &&
		node[2]->type != Node_Internal &&
		node[3]->type != Node_Internal)
	{
		ContourProcessEdge(node, dir, Indecies, chunk,MaterialIndex);
	}
	else
	{
		for (int i = 0; i < 2; i++)
		{
			ChunkOctreeNode* edgeNodes[4];
			const int c[4] =
			{
				edgeProcEdgeMask[dir][i][0],
				edgeProcEdgeMask[dir][i][1],
				edgeProcEdgeMask[dir][i][2],
				edgeProcEdgeMask[dir][i][3],
			};

			for (int j = 0; j < 4; j++)
			{
				if (node[j]->type == Node_Leaf || node[j]->type == Node_Pseudo)
				{
					edgeNodes[j] = node[j];
				}
				else
				{
					edgeNodes[j] = node[j]->Children[c[j]];
				}
			}
		ContourEdgeProc(edgeNodes, edgeProcEdgeMask[dir][i][4], Indecies,chunk,MaterialIndex);
		}
	}
}

void ContourFaceProc(ChunkOctreeNode* node[2], int dir, TArray<int32>* Indecies, AChunkMesh* chunk, int MaterialIndex) {
	if (!node[0] || !node[1])
	{
		return;
	}

	if (node[0]->type == Node_Internal ||
		node[1]->type == Node_Internal)
	{
		for (int i = 0; i < 4; i++)
		{
			ChunkOctreeNode* faceNodes[2];
			const int c[2] =
			{
				faceProcFaceMask[dir][i][0],
				faceProcFaceMask[dir][i][1],
			};

			for (int j = 0; j < 2; j++)
			{
				if (node[j]->type != Node_Internal)
				{
					faceNodes[j] = node[j];
				}
				else
				{
					faceNodes[j] = node[j]->Children[c[j]];
				}
			}

			ContourFaceProc(faceNodes, faceProcFaceMask[dir][i][2], Indecies,chunk,MaterialIndex);
		}

		const int orders[2][4] =
		{
			{ 0, 0, 1, 1 },
			{ 0, 1, 0, 1 },
		};
		for (int i = 0; i < 4; i++)
		{
			ChunkOctreeNode* edgeNodes[4];
			const int c[4] =
			{
				faceProcEdgeMask[dir][i][1],
				faceProcEdgeMask[dir][i][2],
				faceProcEdgeMask[dir][i][3],
				faceProcEdgeMask[dir][i][4],
			};

			const int* order = orders[faceProcEdgeMask[dir][i][0]];
			for (int j = 0; j < 4; j++)
			{
				if (node[order[j]]->type == Node_Leaf ||
					node[order[j]]->type == Node_Pseudo)
				{
					edgeNodes[j] = node[order[j]];
				}
				else
				{
					edgeNodes[j] = node[order[j]]->Children[c[j]];
				}
			}

			ContourEdgeProc(edgeNodes, faceProcEdgeMask[dir][i][5], Indecies,chunk,MaterialIndex);
		}
	}
}

void ContourCellProc(ChunkOctreeNode* node, TArray<int32>* Indecies,AChunkMesh* chunk,int MaterialIndex) {
	if (node == nullptr) {
		return;
	}
	if(node->type == Node_Internal){
		for (int i = 0; i < 8; i++) {
			ContourCellProc(node->Children[i], Indecies,chunk, MaterialIndex);
		}
		for (int i = 0; i < 12; i++)
		{
			ChunkOctreeNode* faceNodes[2];
			const int c[2] = { cellProcFaceMask[i][0], cellProcFaceMask[i][1] };

			faceNodes[0] = node->Children[c[0]];
			faceNodes[1] = node->Children[c[1]];
			ContourFaceProc(faceNodes, cellProcFaceMask[i][2], Indecies,chunk,MaterialIndex);
		}

		for (int i = 0; i < 6; i++)
		{
			ChunkOctreeNode* edgeNodes[4];
			const int c[4] =
			{
				cellProcEdgeMask[i][0],
				cellProcEdgeMask[i][1],
				cellProcEdgeMask[i][2],
				cellProcEdgeMask[i][3],
			};

			for (int j = 0; j < 4; j++)
			{
				edgeNodes[j] = node->Children[c[j]];
			}

			ContourEdgeProc(edgeNodes, cellProcEdgeMask[i][4], Indecies, chunk,MaterialIndex);
		}
	}
}

void TraverseOctree(ChunkOctreeNode* root, TArray<FVector>* Vertecies, TArray<FVector>* Normals , AChunkMesh* chunk, int MaterialIndex) {
	if (root == nullptr)
		return;
	if (root->type == Node_Leaf || root->type == Node_Pseudo) {
		if (root->DrawInfo != nullptr) {
			bool containsMaterial = false;
			for (int i = 0; i < 8; i++) {
				if (root->DrawInfo->Materials[i] == MaterialIndex) {
					containsMaterial = true;
					break;
				}
			}
			if (containsMaterial) {
				root->DrawInfo->index = TriangleIndex++;
				Vertecies->Add(root->DrawInfo->Position);
				Normals->Add(root->DrawInfo->AverageNormal);
			}
		}
	}
	else
		for (int i = 0; i < 8; i++) {
			if(root->type == Node_Internal)
				TraverseOctree(root->Children[i], Vertecies, Normals, chunk, MaterialIndex);
		
		}
	//if (root->type == Node_Leaf || root->type == Node_Pseudo) 
	/*if(root->DrawInfo!=nullptr){
	}*/
	//index = 0;
	return;
}

void AChunkMesh::ConstructMesh() {
	if (Objects.Num() == 0)
		return;
	VoxelOctree->root = new ChunkOctreeNode(ChunkBounds,this);
	VoxelOctree->root->type = Node_Internal;
	SYSTEMTIME start, stop;
	GetSystemTime(&start);
	VoxelOctree->root->ConstructOctreeNodes();
	for (int i = 0; i < MaterialList.Num(); i++) {
		TArray<FVector> Vertecies;
		TArray<int32> Indecies;
		TArray<FVector> Normals;
		TArray<FVector2D> UVs;
		TArray<FLinearColor> Colors;
		TArray<FProcMeshTangent> Tangents;
		TriangleIndex = 0;
		TraverseOctree(VoxelOctree->root, &Vertecies, &Normals, this, i);
		ContourCellProc(VoxelOctree->root, &Indecies, this, i);
		Mesh->CreateMeshSection_LinearColor(i, Vertecies, Indecies, Normals, UVs, Colors, Tangents, true);
		Mesh->SetMaterial(i, MaterialList[i]);
	}
	GetSystemTime(&stop);
	GEngine->AddOnScreenDebugMessage(0, 10, FColor::Red, FString::FromInt((stop.wSecond-start.wSecond)*1000 + stop.wMilliseconds - start.wMilliseconds));
	delete VoxelOctree->root;

}

void AChunkMesh::UpdateObjects() {
	FTransform transform = this->GetTransform();
	ChunkBounds = FBox(transform.GetTranslation(), transform.GetTranslation() + ChunkSize);//Don't forget. All operations in sequence;
	ULevel * level;
	level = this->GetLevel();
	AWorldObjectsHandler* handler;
//	int32 index;
	level->Actors.FindItemByClass<AWorldObjectsHandler>(&handler);
	if (!handler->HasActorBegunPlay())
		handler->DispatchBeginPlay();
	this->Objects = handler->ObjectsContainer.Search(ChunkBounds);
}
VoxelVertexData AChunkMesh::CalcCSGAtPoint(const FVector & Point)
{
	float current, previous;
	current = MAX_FLT;
	previous = MAX_FLT;
	CSG_TYPE_ENUM CurrentCSGOp;
	ITerrainObjectInterface* currentObject;
	FDateTime previousTimeStamp = FDateTime::MinValue();
	for (ITerrainObjectInterface* obj : Objects) {
		current = (obj->CalculateSignedDistanceFunction(Point/*-GetTransform().GetTranslation()*/));
		CurrentCSGOp = obj->GetCSGType();
		/*if (!(obj->GetBoundingBox().IsInsideOrOn(Point)))
			continue;*/
		switch (CurrentCSGOp) {
		case CSG_TYPE_ENUM::CSG_OR: {
			if (current < previous) {
				if (obj->GetTimeStamp() >= previousTimeStamp) {
					previousTimeStamp = obj->GetTimeStamp();
					previous = current;
					currentObject = obj;
				}
			}
			break;
		}
		case CSG_TYPE_ENUM::CSG_DIFF: {
			if ((-current) > previous) {
				if (obj->GetTimeStamp() >= previousTimeStamp) {
					previousTimeStamp = obj->GetTimeStamp();
					currentObject = obj;
					previous = -current;
				}
			}
			break;
		}
		}
	}
	if (previous <= 0)
		return VoxelVertexData(previous, currentObject);
	else
		return VoxelVertexData(previous, nullptr);
}
// Called when the game starts or when spawned
void AChunkMesh::BeginPlay()
{
	Super::BeginPlay();
	VoxelOctree = new vOctree();
/*	float resolution = 64;
	int VoxelSize = 200;*/
	//Thread = new FMeshCalculationMultithread();
	UpdateObjects();
	ConstructMesh();
	
}

// Called every frame
void AChunkMesh::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);
}






//float UnitPerVoxel = 200;
//float HalfChunkSize = 3125.0;
//float HalfChunkSize = 6400;

/*
	Code comments

	Vertecies - final array of vertecies, which will be sent to CreateMeshSection func as a parameter;
	Indecies - Array of indecies;
	Normals - Array of normals to each vertex;
	UVs - Array of UV coordinates of each vertex;
	Colors - Array of vertex colors;
	Tangents - needs more research;

	grid - 3D array of grid popint data, consisting of value at grid point and a pointer to the object (Terrain object like Heightmap, sphere, parallelepiped, etc), which generated that value;
	if value is lesser than 0, grid point lies within the object, otherwise outside;
	zero represents the isosurface; (Surface of the object);

	edges - 4D (3 directions: x,y,z, and normal 3D arrays) which store edge data. Data is stored if the isosuface intersects that edge.
	Otherwise the element is nullptr;

	voxels - 3d Array of vertex data which is vertex position, normals at this vertex, UVs.
	if there is no vertex, data is nullptr;

	Workflow:
		firstly, for each grid point we evaluate signed distance function. Right now we combine all objects.
		Evaluation consists of finding the object with the least value, and writing that object pointer to grid point.

		secondly, for each edge we check the edge for the sign change performing 0ne multiplication of the value on its ends.
		if the value is lesse than zero, we find t between a and b value, so that a+(b-a)*t = 0 between edge points to find the intersection point.
		than we calculate gradient of the SDF at the intersection point, which is a normal to the isosurface at that point.

		thirdly for each voxel with a sign change, which is found by checking each voxel vertex with previous,
		we find the minimizer of the quadratic error function of points and normals;

		finally, for each edge with a sign change, we construct a polygon form four nearest voxels;

*/


void ChunkOctreeNode::ConstructOctreeNodes(){

	FVector Min = this->BoundingBox.GetCenter() - this->BoundingBox.GetExtent();
//	ITerrainObjectInterface* VerteciesMaterials[8];
	//Check whether this node is intersected by the isosurface
	this->size = this->BoundingBox.GetExtent().X * 2;
	if (size > minsize) {
		this->type = Node_Internal;
		FVector MinBound, MaxBound;
		for (int nodeCount = 0; nodeCount < 8; nodeCount++) {
			MinBound = Min + (VoxelVertecies[nodeCount] * BoundingBox.GetExtent());
			MaxBound = MinBound + BoundingBox.GetExtent();
			FBox box = FBox(MinBound, MaxBound);
			Children[nodeCount] = new ChunkOctreeNode(box, this->Chunk);;
			Children[nodeCount]->Parent = this;
			Children[nodeCount]->ConstructOctreeNodes();
		}
		for (int i = 0; i < 8; i++) {
			if (nullptr != Children[i]) {
				if (Children[i]->type == Node_Leaf) {
					if (Children[i]->DrawInfo == nullptr) {
						delete Children[i];
						Children[i] = nullptr;
					}
				}
				else {
					bool NodeIntersectedFlag = false;
					for (int j = 0; j < 8; j++) {
						//UE_LOG(LogTemp, Verbose, FString::Printf("%s%i", "Node_Internal ", j));
						NodeIntersectedFlag |= (Children[i]->Children[j] != nullptr);
					}
					if (!NodeIntersectedFlag) {
						if (Children != nullptr)
							if (Children[i] != nullptr) {
								delete Children[i];
								Children[i] = nullptr;
							}
					}
				}
			}

				
		}
			
	}


	else {
		VoxelVertexData current;
		VoxelVertexData VerteciesValue[8];
		FVector firstPoint, secondPoint;
		FVector IntersectionPositions[12];
		FVector Normals[12];
		this->type = OctreeNodeType::Node_Leaf;
		this->DrawInfo = new OctreeDrawInfo();
		for (int i = 0; i < 8; i++) {
			current = Chunk->CalcCSGAtPoint(Min + ((BoundingBox.GetExtent()*VoxelVertecies[i] * 2)));
			VerteciesValue[i] = current;
			if (current.first< 0) {//Still bugged as hell. Need some other condition for inside/outside checking
				this->DrawInfo->corners |= 1<<i;
			}
		}
		if (this->DrawInfo->corners == 0 || this->DrawInfo->corners == 255) {
			if (nullptr != this->DrawInfo)
			{
				delete this->DrawInfo;
				DrawInfo = nullptr;
			}
			return;
		}
		for (int i = 0; i < 8; i++) {
			if (VerteciesValue[i].first > 0) {
				DrawInfo->Materials[i] = -1;
			}
			else {
				DrawInfo->Materials[i] = 0;
			}
		}
		int counter = 0;
		for (int i = 0; i < 12; i++) {
			if (VerteciesValue[edgevmap[i][0]].first * VerteciesValue[edgevmap[i][1]].first <= 0) {
				firstPoint = Min + VoxelVertecies[edgevmap[i][0]];
				secondPoint = VoxelVertecies[edgevmap[i][1]] * minsize;
				IntersectionPositions[counter] = ((firstPoint + (secondPoint*(AChunkMesh::lerpEdge(VerteciesValue[edgevmap[i][0]].first, VerteciesValue[edgevmap[i][1]].first)))));
				if (VerteciesValue[edgevmap[i][0]].first <= 0) {
					Normals[counter++] = (VerteciesValue[edgevmap[i][0]].second->CalculateGradientAtPoint(IntersectionPositions[counter]));
				}
				else {
					Normals[counter++] = (VerteciesValue[edgevmap[i][1]].second->CalculateGradientAtPoint(IntersectionPositions[counter]));
				}
			}
		}
		for (int i = 0; i < counter; i++) {
			DrawInfo->QEFpos[i * 3] = IntersectionPositions[i].X;
			DrawInfo->QEFpos[i * 3 + 1] = IntersectionPositions[i].Y;
			DrawInfo->QEFpos[i * 3 + 2] = IntersectionPositions[i].Z;
			DrawInfo->QEFnormals[i * 3] = Normals[i].X;
			DrawInfo->QEFnormals[i * 3 + 1] = Normals[i].Y;
			DrawInfo->QEFnormals[i * 3 + 2] = Normals[i].Z;
			DrawInfo->AverageNormal += Normals[i];
		}

		float result[3];
		DrawInfo->AverageNormal.Normalize();
		qef_solve_from_points_3d(DrawInfo->QEFpos, DrawInfo->QEFnormals, counter, result);
		DrawInfo->Position.X = result[0];
		DrawInfo->Position.Y = result[1];
		DrawInfo->Position.Z = result[2];
		DrawInfo->Position -= Chunk->GetTransform().GetTranslation();
		return;
	}

	
}
