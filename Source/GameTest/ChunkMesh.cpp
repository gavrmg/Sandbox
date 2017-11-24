// Fill out your copyright notice in the Description page of Project Settings.

#include "ChunkMesh.h"
#include "qef_simd.h"
#include "WorldObjectsHandler.h"
#include "SphereDestructionObject.h"
#include "ConstructorHelpers.h"

// Sets default values

const FVector VoxelVertecies[] = { FVector(0, 0, 0),  FVector(1, 0, 0),
FVector(1, 0, 1),  FVector(0, 0, 1),  FVector(0, 1, 0),  FVector(1, 1, 0),
FVector(1, 1, 1),  FVector(0, 1, 1) };

const FVector VoxelEdges[3][4] = { {FVector(0,0,0),FVector(0,0,1),FVector(0,1,1),FVector(0,1,0)},{FVector(0,0,0),FVector(0,0,1),FVector(1,0,1),FVector(1,0,0)},{FVector(0,0,0),FVector(0,1,0),FVector(1,1,0),FVector(1,0,0)} };
const FVector NearestVoxels[3][4] = { { FVector(0,0,0),FVector(0,-1,0),FVector(0,-1,-1),FVector(0,0,-1) },{ FVector(0,0,0),FVector(0,0,-1),FVector(-1,0,-1),FVector(-1,0,0) },{ FVector(0,0,0),FVector(-1,0,0),FVector(-1,-1,0),FVector(0,-1,0) } };
AChunkMesh::AChunkMesh()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;
	Mesh = CreateDefaultSubobject<UProceduralMeshComponent>(TEXT("Mesh"));
	MaterialList.Add((ConstructorHelpers::FObjectFinder<UMaterial>(TEXT("UMaterial'/Game/Resources/Materials/TestStone'"))).Object);
	MaterialList.Add((ConstructorHelpers::FObjectFinder<UMaterial>(TEXT("UMaterial'/Game/Resources/Materials/TestDirt'"))).Object);
	RootComponent = Mesh;
	Thread->Chunk = this;
	Thread->ThisChunkData = new ChunkData();

}

void AChunkMesh::ConstructMesh()
{
	Thread->BuildMesh(true);
}

void AChunkMesh::UpdateObjects() {
	FTransform transform = this->GetTransform();
	ChunkBounds = FBox(transform.GetTranslation() - (ChunkSize/2), transform.GetTranslation() + (ChunkSize/2));//Don't forget. All operations in sequence;
	ULevel * level;
	level = this->GetLevel();
	AWorldObjectsHandler* handler;
//	int32 index;
	level->Actors.FindItemByClass<AWorldObjectsHandler>(&handler);
	if (!handler->HasActorBegunPlay())
		handler->DispatchBeginPlay();
	this->Objects = handler->ObjectsContainer.Search(ChunkBounds);
}
// Called when the game starts or when spawned
void AChunkMesh::BeginPlay()
{
	Super::BeginPlay();
/*	float resolution = 64;
	int VoxelSize = 200;*/
	UpdateObjects();
	ConstructMesh();
	
}

// Called every frame
void AChunkMesh::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

	if (IsRebuilding) {
		if ((Thread->GetIsGridComplete())&&MeshDataBuilding == false) {
			MeshDataBuilding = true;
			for(int i = 0; i < MaterialList.Num(); i++)
				Thread->MeshSectionCalculation_CompleteEvents.Add(TGraphTask<FMeshCalculationMultithread::FMeshDataBuildingTask>::CreateTask(NULL, ENamedThreads::GameThread).ConstructAndDispatchWhenReady(i,Thread));
		}
		if (MeshDataBuilding)
			if (Thread->AreCalculationsComplete()) {
				for (int i = 0; i < MaterialList.Num(); i++) {
					Mesh->CreateMeshSection_LinearColor(i, *Thread->OutputArray[i]->Vertecies, *Thread->OutputArray[i]->Indecies, *Thread->OutputArray[i]->Normals, *Thread->OutputArray[i]->UVs, *Thread->OutputArray[i]->Colors, *Thread->OutputArray[i]->Tangents, true);
					Mesh->SetMaterial(i, MaterialList[i]);
				}
				MeshDataBuilding = false;
				IsRebuilding = false;
				Thread->ResetGridData();
				Thread->GridBuilding_CompleteEvent.Empty();
				Thread->MeshSectionCalculation_CompleteEvents.Empty();
				Thread->EmptyOutput();
			}

	}
}

/*void AChunkMesh::DataReady_Implementation(AChunkMesh* Chunk, FMeshCalculationThread* Thread)
{

}*/

TArray<float> b;
float temp;
long elapsed, current, previous;



float UnitPerVoxel = 200;
//float HalfChunkSize = 3125.0;
float HalfChunkSize = 6400;

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




/*uint32 FMeshCalculationThread::Run()
{
	elapsed = 0;
	TArray<FVector> normals;
	TArray<FVector> positions;
	GridData**** grid;
	EdgeData***** edges;
	Vertex**** voxels;
	FVector temp,NextPoint;
	FVector CurrentPos;
	FVector IntersectionPosition;
	FVector CurrentEdgePos;
	FVector CurrentVoxel;
	Vertex* CurrentVertex;
	float relativePos; //Relative position of the edge's intersection point
	float current, previous,next;//Temp floats used for calculateing SDF value at grid points
	EdgeData* currentEdge;
	ITerrainObjectInterface* currentObject = nullptr;
	bool SignChangeFlag = false;
	bool ClockwiseFlag = false;
	//Init grid;
	//Basic size = 64
	//Basic size is the number of vertecies in one dimension of the chunk
	//The number of grid points and edges is size+1
	//Actually the number of edges is size or size+1, but for sake of easy implementaiton is always size+1
	//Used number is size+2 so that space between chunks would also be covered
	grid = new GridData***[BaseResolution+2];
	for (int i = 0; i < BaseResolution+2; i++) {
		grid[i] = new GridData**[BaseResolution+2];
		for (int j = 0; j < BaseResolution+2; j++) {
			grid[i][j] = new GridData*[BaseResolution+2];
			for (int k = 0; k < BaseResolution+2; k++)
				grid[i][j][k] = new GridData();
		}
	}
	//Init grid edges;
	edges = new EdgeData****[3];
	for (int Direction = 0; Direction < 3; Direction++) {
		edges[Direction] = new EdgeData***[BaseResolution+2];
		for (CurrentPos.X = 0; CurrentPos.X < BaseResolution+2; CurrentPos.X++) {
			edges[Direction][(int)CurrentPos.X] = new EdgeData**[BaseResolution+2];
			for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution+2; CurrentPos.Y++) {
				edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y] = new EdgeData*[BaseResolution+2];
				for(CurrentPos.Z = 0; CurrentPos.Z< BaseResolution+2;CurrentPos.Z++){
					edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z] = nullptr;
				}
			}
		}
	}
	voxels = new Vertex***[BaseResolution+1];
	for (int i = 0; i < BaseResolution+1; i++) {
		voxels[i] = new Vertex**[BaseResolution+1];
		for (int j = 0; j < BaseResolution+1; j++) {
			voxels[i][j] = new Vertex*[BaseResolution+1];
			for (int k = 0; k < BaseResolution+1; k++)
				voxels[i][j][k] = nullptr;
		}
	}
	//Calculate values at grid points
	CSG_TYPE_ENUM CurrentCSGOp;//Which CSG operation should be executed
	FDateTime  previousTimeStamp;
	for (CurrentPos.X = 0; CurrentPos.X < BaseResolution+2; CurrentPos.X++)
		for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution+2; CurrentPos.Y++)
			for (CurrentPos.Z = 0; CurrentPos.Z < BaseResolution+2; CurrentPos.Z++) {
				current = MAX_FLT;
				previous = MAX_FLT;
				currentObject = nullptr;
				FVector pos = ((CurrentPos-32)*UnitPerVoxel) + Chunk->ChunkBounds.GetCenter();
				previousTimeStamp = FDateTime::MinValue();
				for (ITerrainObjectInterface* obj : Chunk->Objects) {
					current = (obj->CalculateSignedDistanceFunction(pos));
					CurrentCSGOp = obj->GetCSGType();
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
				
				grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Value = previous;
				grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Object = currentObject;
				grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Material = Material;

			}

	//For each edge, if there is a sign change, calculate position of the intersection point and normal at this point;
	//new edge is created only if there is an intersection, else this edge is nullptr;
	//Used number is size + 2 as we need to look through every edge
	for (int Direction = 0; Direction < 3; Direction++) {
		temp.Set(0, 0, 0);
		temp.Component(Direction) = 1;
		for (CurrentPos.X = 0; CurrentPos.X < BaseResolution+2; CurrentPos.X++)
			for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution+2; CurrentPos.Y++)
				for (CurrentPos.Z = 0; CurrentPos.Z < BaseResolution+2; CurrentPos.Z++) {
					NextPoint = CurrentPos + temp;
					if (NextPoint.GetMax() > BaseResolution+1)
						continue;
					current = grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Value;
					next = grid[(int)NextPoint.X][(int)NextPoint.Y][(int)NextPoint.Z]->Value;
					if (current*next <= 0) {
					edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z] = new EdgeData();
						currentEdge = edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z];
						relativePos = lerpEdge(current, next);
					IntersectionPosition = (((CurrentPos + (temp*relativePos)) - 32)*UnitPerVoxel) + Chunk->ChunkBounds.GetCenter();
						currentEdge->Position = IntersectionPosition;
						if(current<=0)
							currentObject = grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Object;
						else
							currentObject = grid[(int)NextPoint.X][(int)NextPoint.Y][(int)NextPoint.Z]->Object;

						currentEdge->Normal = currentObject->CalculateGradientAtPoint(IntersectionPosition);
						currentEdge->Material = grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Material;
					}
				}
	}
	float* Positions_container;
	float* Normals_container;
	float* Result = new float[3];
	//Calculating vertex position in each voxel
	//Number of voxels is size+1
	for (CurrentPos.X = 0; CurrentPos.X < BaseResolution+1; CurrentPos.X++)
		for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution+1; CurrentPos.Y++)
			for (CurrentPos.Z = 0; CurrentPos.Z < BaseResolution+1; CurrentPos.Z++) {
				previous = grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Value;
				SignChangeFlag = false;
				for (int VertexCounter = 1; VertexCounter < 8; VertexCounter++) {
					NextPoint = CurrentPos + VoxelVertecies[VertexCounter];
					current = grid[(int)NextPoint.X][(int)NextPoint.Y][(int)NextPoint.Z]->Value;
					if (current*previous <= 0) {
						SignChangeFlag = true;
						break;
					}
				}
				if (SignChangeFlag) {
					normals.Empty();
					positions.Empty();
					for(int Direction = 0; Direction < 3; Direction++)
						for (int EdgeCounter = 0; EdgeCounter < 4; EdgeCounter++) {
							CurrentEdgePos = CurrentPos + VoxelEdges[Direction][EdgeCounter];
							currentEdge = edges[Direction][(int)CurrentEdgePos.X][(int)CurrentEdgePos.Y][(int)CurrentEdgePos.Z];
							if(currentEdge!=nullptr){
								normals.Add(currentEdge->Normal);
								positions.Add((currentEdge->Position));

							}
						}

					voxels[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z] = new Vertex();
					CurrentVertex = voxels[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z];
					Positions_container = new float[positions.Num()*3];
					Normals_container = new float[normals.Num()*3];
					CurrentVertex->Normal = FVector::ZeroVector;
					for (int i = 0; i < normals.Num(); i++) {
						Positions_container[i * 3] = positions[i].X;
						Positions_container[i * 3 + 1] = positions[i].Y;
						Positions_container[i * 3 + 2] = positions[i].Z;
						Normals_container[i * 3] = normals[i].X;
						Normals_container[i * 3 + 1] = normals[i].Y;
						Normals_container[i * 3 + 2] = normals[i].Z;
						CurrentVertex->Normal += normals[i];
					}
					qef_solve_from_points_3d(Positions_container, Normals_container, normals.Num(), Result);
					delete[] Positions_container;
					delete[] Normals_container;
					CurrentVertex->Position.X = Result[0];
					CurrentVertex->Position.Y = Result[1];
					CurrentVertex->Position.Z = Result[2];
					CurrentVertex->Position -= Chunk->ChunkBounds.GetCenter();
					CurrentVertex->Normal.Normalize();
					//Add normal

				}
			}
	delete[] Result;
	//Build mesh
	//There we only use edges from 1 to 64, as the zero edge is used in the previous chunk, and BaseResolution+1th edge has voxels lying in the next chunk
	int MaterialCount = 0;
	//	rebuilding = true;
		TArray<FVector>* Vertecies = new TArray<FVector>();
		TArray<int32>* Indecies = new TArray<int32>();
		TArray<FVector>* Normals = new TArray<FVector>();
		TArray<FVector2D>* UVs = new TArray<FVector2D>();
		TArray<FLinearColor>* Colors = new TArray<FLinearColor>();
		TArray<FProcMeshTangent>* Tangents = new TArray<FProcMeshTangent>();
		for (int Direction = 0; Direction < 3; Direction++) {
			temp.Set(0, 0, 0);
			temp.Component(Direction) = 1;
			for (CurrentPos.X = 1; CurrentPos.X < BaseResolution + 1; CurrentPos.X++)
				for (CurrentPos.Y = 1; CurrentPos.Y < BaseResolution + 1; CurrentPos.Y++)
					for (CurrentPos.Z = 1; CurrentPos.Z < BaseResolution + 1; CurrentPos.Z++) {
						ClockwiseFlag = false;
						currentEdge = edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z];
						if (currentEdge != nullptr && (UMaterial*)currentEdge->Material == Material) {
							if (grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Value >= 0)
								ClockwiseFlag = true;
							for (int VoxelCounter = 0; VoxelCounter < 4; VoxelCounter++) {
								CurrentVoxel = CurrentPos + NearestVoxels[Direction][VoxelCounter];
								Vertecies->Add(voxels[(int)CurrentVoxel.X][(int)CurrentVoxel.Y][(int)CurrentVoxel.Z]->Position);
								Normals->Add(voxels[(int)CurrentVoxel.X][(int)CurrentVoxel.Y][(int)CurrentVoxel.Z]->Normal);
							}
							if (ClockwiseFlag) {
								Indecies->Add(Vertecies->Num() - 4);
								Indecies->Add(Vertecies->Num() - 3);
								Indecies->Add(Vertecies->Num() - 2);
								Indecies->Add(Vertecies->Num() - 2);
								Indecies->Add(Vertecies->Num() - 1);
								Indecies->Add(Vertecies->Num() - 4);
							}
							else
							{
								Indecies->Add(Vertecies->Num() - 4);
								Indecies->Add(Vertecies->Num() - 1);
								Indecies->Add(Vertecies->Num() - 2);
								Indecies->Add(Vertecies->Num() - 2);
								Indecies->Add(Vertecies->Num() - 3);
								Indecies->Add(Vertecies->Num() - 4);

							}
							UVs->Add(FVector2D(0, 0));
							UVs->Add(FVector2D(0, 1));
							UVs->Add(FVector2D(1, 1));
							UVs->Add(FVector2D(1, 0));
						}
					}
		}
	//		Mesh->ClearMeshSection(MaterialCount);
//	Normals->SetNumZeroed(Vertecies->Num());
//	Colors->SetNumZeroed(Vertecies->Num());
//	UVs->SetNumZeroed(Vertecies->Num());
//	Tangents->SetNumZeroed(Vertecies->Num());

	//Mesh->SetMaterial(0,)
	for (int i = 0; i < BaseResolution+2; i++) {
		for (int j = 0; j < BaseResolution+2; j++) {
			for (int k = 0; k < BaseResolution+2; k++)
				delete grid[i][j][k];
		delete[] grid[i][j];
		}
		delete[] grid[i];
	}
	delete[] grid;
	for (int Direction = 0; Direction < 3; Direction++) {
		for (CurrentPos.X = 0; CurrentPos.X < BaseResolution+2; CurrentPos.X++) {
			for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution+2; CurrentPos.Y++) {
				for (CurrentPos.Z = 0; CurrentPos.Z < BaseResolution+2; CurrentPos.Z++) {
					if (edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z] != nullptr)
						delete edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z];
				}
				delete[] edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y];
			}
			delete[] edges[Direction][(int)CurrentPos.X];
		}
		delete[] edges[Direction];
	}
	delete[] edges;

	for (int i = 0; i < BaseResolution+1; i++) {
		for (int j = 0; j < BaseResolution+1; j++) {
			for (int k = 0; k < BaseResolution+1; k++)
				if(voxels[i][j][k]!=nullptr)
					delete voxels[i][j][k];
			delete[] voxels[i][j];
		}
		delete[] voxels[i];
	}
	delete[] voxels;
	hasEnded = true;
	return uint32();
}*/




FMeshCalculationMultithread::FMeshCalculationMultithread()
{
}

FMeshCalculationMultithread::~FMeshCalculationMultithread()
{
}

void FMeshCalculationMultithread::BuildMesh(bool IsInitiating)
{
	OutputArray.SetNumZeroed(Chunk->MaterialList.Num());
	if (IsInitiating) {
		GridBuilding_CompleteEvent.Add(TGraphTask<FGridBuildingTask>::CreateTask(NULL, ENamedThreads::GameThread).ConstructAndDispatchWhenReady(this));
		while (!GetIsGridComplete());
		for (int i = 0; i < Chunk->MaterialList.Num(); i++)
			MeshSectionCalculation_CompleteEvents.Add(TGraphTask<FMeshDataBuildingTask>::CreateTask(NULL, ENamedThreads::GameThread).ConstructAndDispatchWhenReady(i,this));
		while (!AreCalculationsComplete());
		for (int i = 0; i < Chunk->MaterialList.Num(); i++) {
			Chunk->Mesh->CreateMeshSection_LinearColor(i, *OutputArray[i]->Vertecies, *OutputArray[i]->Indecies, *OutputArray[i]->Normals, *OutputArray[i]->UVs, *OutputArray[i]->Colors, *OutputArray[i]->Tangents, true);
			Chunk->Mesh->SetMaterial(i, Chunk->MaterialList[i]);
		}
		GridBuilding_CompleteEvent.Empty();
		MeshSectionCalculation_CompleteEvents.Empty();
		ResetGridData();
		EmptyOutput();

	}
	else {
		Chunk->SetRebuilding(true);
		GridBuilding_CompleteEvent.Add(TGraphTask<FGridBuildingTask>::CreateTask(NULL, ENamedThreads::GameThread).ConstructAndDispatchWhenReady(this));
	}
		
}

bool FMeshCalculationMultithread::GetIsGridComplete()
{
	if (GridBuilding_CompleteEvent[0]->IsComplete()) {
		return true;
	}
	else {
		return false;
	}
}

bool FMeshCalculationMultithread::AreCalculationsComplete()
{
	for (int i = 0; i < MeshSectionCalculation_CompleteEvents.Num(); i++)
		if (!MeshSectionCalculation_CompleteEvents[i]->IsComplete())
			return false;
	return true;
}

void FMeshCalculationMultithread::BuildGrid()
{
	elapsed = 0;
	TArray<FVector> normals;
	TArray<FVector> positions;
	FVector temp, NextPoint;
	FVector CurrentPos;
	FVector IntersectionPosition;
	FVector CurrentEdgePos;
	FVector CurrentVoxel;
//	Vertex* CurrentVertex;
	float relativePos; //Relative position of the edge's intersection point
	float current, previous, next;//Temp floats used for calculateing SDF value at grid points
	EdgeData* currentEdge;
	ITerrainObjectInterface* currentObject = nullptr;
	bool SignChangeFlag = false;
	bool ClockwiseFlag = false;
	//Init grid;
	//Basic size = 64
	//Basic size is the number of vertecies in one dimension of the chunk
	//The number of grid points and edges is size+1
	//Actually the number of edges is size or size+1, but for sake of easy implementaiton is always size+1
	//Used number is size+2 so that space between chunks would also be covered
	
	ThisChunkData->grid = new GridData***[BaseResolution + 2];
	for (int i = 0; i < BaseResolution + 2; i++) {
		ThisChunkData->grid[i] = new GridData**[BaseResolution + 2];
		for (int j = 0; j < BaseResolution + 2; j++) {
			ThisChunkData->grid[i][j] = new GridData*[BaseResolution + 2];
			for (int k = 0; k < BaseResolution + 2; k++)
				ThisChunkData->grid[i][j][k] = new GridData();
		}
	}
	//Init grid edges;
	ThisChunkData->edges = new EdgeData****[3];
	for (int Direction = 0; Direction < 3; Direction++) {
		ThisChunkData->edges[Direction] = new EdgeData***[BaseResolution + 2];
		for (CurrentPos.X = 0; CurrentPos.X < BaseResolution + 2; CurrentPos.X++) {
			ThisChunkData->edges[Direction][(int)CurrentPos.X] = new EdgeData**[BaseResolution + 2];
			for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution + 2; CurrentPos.Y++) {
				ThisChunkData->edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y] = new EdgeData*[BaseResolution + 2];
				for (CurrentPos.Z = 0; CurrentPos.Z< BaseResolution + 2; CurrentPos.Z++) {
					ThisChunkData->edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z] = nullptr;
				}
			}
		}
	}
	ThisChunkData->voxels = new Vertex***[BaseResolution + 1];
	for (int i = 0; i < BaseResolution + 1; i++) {
		ThisChunkData->voxels[i] = new Vertex**[BaseResolution + 1];
		for (int j = 0; j < BaseResolution + 1; j++) {
			ThisChunkData->voxels[i][j] = new Vertex*[BaseResolution + 1];
			for (int k = 0; k < BaseResolution + 1; k++)
				ThisChunkData->voxels[i][j][k] = nullptr;
		}
	}
	//Calculate values at grid points
	CSG_TYPE_ENUM CurrentCSGOp;//Which CSG operation should be executed
	FDateTime  previousTimeStamp;
	for (CurrentPos.X = 0; CurrentPos.X < BaseResolution + 2; CurrentPos.X++)
		for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution + 2; CurrentPos.Y++)
			for (CurrentPos.Z = 0; CurrentPos.Z < BaseResolution + 2; CurrentPos.Z++) {
				current = MAX_FLT;
				previous = MAX_FLT;
				currentObject = nullptr;
				FVector pos = ((CurrentPos - 32)*UnitPerVoxel) + Chunk->ChunkBounds.GetCenter();
				previousTimeStamp = FDateTime::MinValue();
				for (ITerrainObjectInterface* obj : Chunk->Objects) {
					current = (obj->CalculateSignedDistanceFunction(pos));
					CurrentCSGOp = obj->GetCSGType();
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

				ThisChunkData->grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Value = previous;
				ThisChunkData->grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Object = currentObject;
				ThisChunkData->grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Material = Chunk->MaterialList[0];

			}

	//For each edge, if there is a sign change, calculate position of the intersection point and normal at this point;
	//new edge is created only if there is an intersection, else this edge is nullptr;
	//Used number is size + 2 as we need to look through every edge
	for (int Direction = 0; Direction < 3; Direction++) {
		temp.Set(0, 0, 0);
		temp.Component(Direction) = 1;
		for (CurrentPos.X = 0; CurrentPos.X < BaseResolution + 2; CurrentPos.X++)
			for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution + 2; CurrentPos.Y++)
				for (CurrentPos.Z = 0; CurrentPos.Z < BaseResolution + 2; CurrentPos.Z++) {
					NextPoint = CurrentPos + temp;
					if (NextPoint.GetMax() > BaseResolution + 1)
						continue;
					current = ThisChunkData->grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Value;
					next = ThisChunkData->grid[(int)NextPoint.X][(int)NextPoint.Y][(int)NextPoint.Z]->Value;
					if (current*next <= 0) {
						ThisChunkData->edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z] = new EdgeData();
						currentEdge = ThisChunkData->edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z];
						relativePos = lerpEdge(current, next);
						IntersectionPosition = (((CurrentPos + (temp*relativePos)) - 32)*UnitPerVoxel)/* - (ChunkSize / 2)*/ + Chunk->ChunkBounds.GetCenter();
						currentEdge->Position = IntersectionPosition;
						if (current <= 0)
							currentObject = ThisChunkData->grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Object;
						else
							currentObject = ThisChunkData->grid[(int)NextPoint.X][(int)NextPoint.Y][(int)NextPoint.Z]->Object;

						currentEdge->Normal = currentObject->CalculateGradientAtPoint(IntersectionPosition);
						currentEdge->Material = ThisChunkData->grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Material;
					}
				}
	}

//	return ChunkData();
}

MeshData* FMeshCalculationMultithread::CalculateMeshSection(int32 SectionNumber)
{
	FVector temp;
	FVector CurrentVoxel;
	bool ClockwiseFlag;
	EdgeData* currentEdge;
	FVector CurrentPos;
	FVector CurrentEdgePos;
	FVector NextPoint;
	float* Positions_container;
	float* Normals_container;
	float* Result = new float[3];
	bool SignChangeFlag;
	Vertex* CurrentVertex;
	TArray<FVector> normals, positions;
	//Calculating vertex position in each voxel
	//Number of voxels is size+1
	for (CurrentPos.X = 0; CurrentPos.X < BaseResolution + 1; CurrentPos.X++)
		for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution + 1; CurrentPos.Y++)
			for (CurrentPos.Z = 0; CurrentPos.Z < BaseResolution + 1; CurrentPos.Z++) {
				previous = ThisChunkData->grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Value;
				SignChangeFlag = false;
				for (int VertexCounter = 1; VertexCounter < 8; VertexCounter++) {
					NextPoint = CurrentPos + VoxelVertecies[VertexCounter];
					current = ThisChunkData->grid[(int)NextPoint.X][(int)NextPoint.Y][(int)NextPoint.Z]->Value;
					if (current*previous <= 0) {
						SignChangeFlag = true;
						break;
					}
				}
				if (SignChangeFlag) {
					normals.Empty();
					positions.Empty();
					for (int Direction = 0; Direction < 3; Direction++)
						for (int EdgeCounter = 0; EdgeCounter < 4; EdgeCounter++) {
							CurrentEdgePos = CurrentPos + VoxelEdges[Direction][EdgeCounter];
							currentEdge = ThisChunkData->edges[Direction][(int)CurrentEdgePos.X][(int)CurrentEdgePos.Y][(int)CurrentEdgePos.Z];
							if (currentEdge != nullptr) {
								normals.Add(currentEdge->Normal);
								positions.Add((currentEdge->Position));

							}
						}

					ThisChunkData->voxels[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z] = new Vertex();
					CurrentVertex = ThisChunkData->voxels[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z];
					Positions_container = new float[positions.Num() * 3];
					Normals_container = new float[normals.Num() * 3];
					CurrentVertex->Normal = FVector::ZeroVector;
					for (int i = 0; i < normals.Num(); i++) {
						Positions_container[i * 3] = positions[i].X;
						Positions_container[i * 3 + 1] = positions[i].Y;
						Positions_container[i * 3 + 2] = positions[i].Z;
						Normals_container[i * 3] = normals[i].X;
						Normals_container[i * 3 + 1] = normals[i].Y;
						Normals_container[i * 3 + 2] = normals[i].Z;
						CurrentVertex->Normal += normals[i];
					}
					qef_solve_from_points_3d(Positions_container, Normals_container, normals.Num(), Result);
					delete[] Positions_container;
					delete[] Normals_container;
					CurrentVertex->Position.X = Result[0];
					CurrentVertex->Position.Y = Result[1];
					CurrentVertex->Position.Z = Result[2];
					CurrentVertex->Position -= Chunk->ChunkBounds.GetCenter();
					CurrentVertex->Normal.Normalize();
					//Add normal

				}
			}
	delete[] Result;
	//Build mesh
	//There we only use edges from 1 to 64, as the zero edge is used in the previous chunk, and BaseResolution+1th edge has voxels lying in the next chunk
	int MaterialCount = 0;
	//	rebuilding = true;
	TArray<FVector>* Vertecies = new TArray<FVector>();
	TArray<int32>* Indecies = new TArray<int32>();
	TArray<FVector>* Normals = new TArray<FVector>();
	TArray<FVector2D>* UVs = new TArray<FVector2D>();
	TArray<FLinearColor>* Colors = new TArray<FLinearColor>();
	TArray<FProcMeshTangent>* Tangents = new TArray<FProcMeshTangent>();
	/*		if((Mesh->GetNumSections()!=0) &&(Mesh->GetProcMeshSection(MaterialCount)!=nullptr))
	Mesh->GetProcMeshSection(MaterialCount)->Reset();*/
	for (int Direction = 0; Direction < 3; Direction++) {
		temp.Set(0, 0, 0);
		temp.Component(Direction) = 1;
		for (CurrentPos.X = 1; CurrentPos.X < BaseResolution + 1; CurrentPos.X++)
			for (CurrentPos.Y = 1; CurrentPos.Y < BaseResolution + 1; CurrentPos.Y++)
				for (CurrentPos.Z = 1; CurrentPos.Z < BaseResolution + 1; CurrentPos.Z++) {
					ClockwiseFlag = false;
					currentEdge = ThisChunkData->edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z];
					if (currentEdge != nullptr && (UMaterial*)currentEdge->Material == Chunk->MaterialList[SectionNumber]) {
						if (ThisChunkData->grid[(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z]->Value >= 0)
							ClockwiseFlag = true;
						for (int VoxelCounter = 0; VoxelCounter < 4; VoxelCounter++) {
							CurrentVoxel = CurrentPos + NearestVoxels[Direction][VoxelCounter];
							Vertecies->Add(ThisChunkData->voxels[(int)CurrentVoxel.X][(int)CurrentVoxel.Y][(int)CurrentVoxel.Z]->Position);
							Normals->Add(ThisChunkData->voxels[(int)CurrentVoxel.X][(int)CurrentVoxel.Y][(int)CurrentVoxel.Z]->Normal);
						}
						if (ClockwiseFlag) {
							Indecies->Add(Vertecies->Num() - 4);
							Indecies->Add(Vertecies->Num() - 3);
							Indecies->Add(Vertecies->Num() - 2);
							Indecies->Add(Vertecies->Num() - 2);
							Indecies->Add(Vertecies->Num() - 1);
							Indecies->Add(Vertecies->Num() - 4);
						}
						else
						{
							Indecies->Add(Vertecies->Num() - 4);
							Indecies->Add(Vertecies->Num() - 1);
							Indecies->Add(Vertecies->Num() - 2);
							Indecies->Add(Vertecies->Num() - 2);
							Indecies->Add(Vertecies->Num() - 3);
							Indecies->Add(Vertecies->Num() - 4);

						}
						UVs->Add(FVector2D(0, 0));
						UVs->Add(FVector2D(0, 1));
						UVs->Add(FVector2D(1, 1));
						UVs->Add(FVector2D(1, 0));
					}
				}
	}
	
	return new MeshData(Vertecies,Indecies,Normals,UVs,new TArray<FLinearColor>(),new TArray<FProcMeshTangent>());
}

void FMeshCalculationMultithread::ResetGridData()
{
	FVector CurrentPos;
	for (int i = 0; i < BaseResolution + 2; i++) {
		for (int j = 0; j < BaseResolution + 2; j++) {
			for (int k = 0; k < BaseResolution + 2; k++)
				delete ThisChunkData->grid[i][j][k];
			delete[]  ThisChunkData->grid[i][j];
		}
		delete[] ThisChunkData->grid[i];
	}
	delete[] ThisChunkData->grid;
	for (int Direction = 0; Direction < 3; Direction++) {
		for (CurrentPos.X = 0; CurrentPos.X < BaseResolution + 2; CurrentPos.X++) {
			for (CurrentPos.Y = 0; CurrentPos.Y < BaseResolution + 2; CurrentPos.Y++) {
				for (CurrentPos.Z = 0; CurrentPos.Z < BaseResolution + 2; CurrentPos.Z++) {
					if (ThisChunkData->edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z] != nullptr)
						delete ThisChunkData->edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y][(int)CurrentPos.Z];
				}
				delete[] ThisChunkData->edges[Direction][(int)CurrentPos.X][(int)CurrentPos.Y];
			}
			delete[] ThisChunkData->edges[Direction][(int)CurrentPos.X];
		}
		delete[] ThisChunkData->edges[Direction];
	}
	delete[] ThisChunkData->edges;

	for (int i = 0; i < BaseResolution + 1; i++) {
		for (int j = 0; j < BaseResolution + 1; j++) {
			for (int k = 0; k < BaseResolution + 1; k++)
				if (ThisChunkData->voxels[i][j][k] != nullptr)
					delete ThisChunkData->voxels[i][j][k];
			delete[] ThisChunkData->voxels[i][j];
		}
		delete[] ThisChunkData->voxels[i];
	}
	delete[] ThisChunkData->voxels;

}

FMeshCalculationMultithread::FGridBuildingTask::FGridBuildingTask(FMeshCalculationMultithread* ThreadDataContainer)
{
	base = ThreadDataContainer;
}

void FMeshCalculationMultithread::FGridBuildingTask::DoTask(ENamedThreads::Type CurrentThread, const FGraphEventRef & CompletionGraph)
{
	base->BuildGrid();
}

FMeshCalculationMultithread::FMeshDataBuildingTask::FMeshDataBuildingTask(int32 SectionNumber, FMeshCalculationMultithread* ThreadDataContainer)
{
	sectionNumber = SectionNumber;
	base = ThreadDataContainer;
}

void FMeshCalculationMultithread::FMeshDataBuildingTask::DoTask(ENamedThreads::Type CurrentThread, const FGraphEventRef & CompletionGraph)
{
	base->OutputArray[sectionNumber] =  base->CalculateMeshSection(sectionNumber);
}
