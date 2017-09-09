__kernel void FastSquareRoot(__global int* message)
{
	// получаем текущий id.
	int gid = get_global_id(0);
	float x = 64;
	float xhalf = x/2;
	int i = *(int*)&x;
	i = 0x5f3759df-(i>>1);
	x = *(float*)&i;
	
	//printf("%f",gid);
//	int gid = 0;
	message[gid] = x;
}